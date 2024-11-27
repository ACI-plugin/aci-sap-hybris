/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.aci.actions.returns;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.service.ACIPaymentService;
import com.hybris.aci.service.ACIRefundAmountCalculationService;
import com.hybris.aci.util.LogHelper;


/**
 * Mock implementation for refunding the money to the customer for the ReturnRequest
 */
public class ACICaptureRefundAction extends AbstractSimpleDecisionAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(ACICaptureRefundAction.class);
	private ACIPaymentService aciPaymentService;
	private ACIRefundAmountCalculationService aciRefundAmountCalculationService;


	/**
	 * @return the aciPaymentService
	 */
	public ACIPaymentService getAciPaymentService()
	{
		return aciPaymentService;
	}


	/**
	 * @param aciPaymentService
	 *           the aciPaymentService to set
	 */
	public void setAciPaymentService(final ACIPaymentService aciPaymentService)
	{
		this.aciPaymentService = aciPaymentService;
	}


	/**
	 * @return the aciRefundAmountCalculationService
	 */
	public ACIRefundAmountCalculationService getAciRefundAmountCalculationService()
	{
		return aciRefundAmountCalculationService;
	}


	/**
	 * @param aciRefundAmountCalculationService
	 *           the aciRefundAmountCalculationService to set
	 */
	public void setAciRefundAmountCalculationService(final ACIRefundAmountCalculationService aciRefundAmountCalculationService)
	{
		this.aciRefundAmountCalculationService = aciRefundAmountCalculationService;
	}


	@Override
	public Transition executeAction(final ReturnProcessModel process)
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();

		// This is a sample reference code. Implement the logic to refund the money to the customer


		LogHelper.debugLog(LOG, "Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final List<PaymentTransactionModel> transactions = returnRequest.getOrder().getPaymentTransactions();

		if (transactions.isEmpty())
		{
			LOG.warn("Unable to refund for ReturnRequest " + returnRequest.getCode() + ", no PaymentTransactions found");
			return Transition.NOK;
		}

		//This assumes that the Order only has one PaymentTransaction
		final PaymentTransactionModel transaction = transactions.get(0);

		final BigDecimal customRefundAmount = aciRefundAmountCalculationService.getCustomRefundAmount(returnRequest);
		BigDecimal amountToRefund = null;

		if (customRefundAmount != null && customRefundAmount.compareTo(BigDecimal.ZERO) > 0)
		{
			amountToRefund = customRefundAmount;
		}
		else
		{
			amountToRefund = aciRefundAmountCalculationService.getOriginalRefundAmount(returnRequest);
		}

		try
		{
			//Send the refund API request
			final ACIPaymentProcessResponse response = getAciPaymentService().refund(returnRequest.getOrder(), transaction,
					String.format("%.2f", amountToRefund));

			LOG.info("Refund request status: " + response.getResult().getDescription());
			if (response.isOk())
			{
				final ReturnStatus returnStatus = ReturnStatus.PAYMENT_REVERSED;
				returnRequest.setStatus(returnStatus);
				returnRequest.getReturnEntries().stream().forEach(entry -> {
					entry.setStatus(returnStatus);
					getModelService().save(entry);
				});
				getModelService().save(returnRequest);
				return Transition.OK;
			}
		}
		catch (final AdapterException e)
		{
			LOG.error("Unable to refund for ReturnRequest " + returnRequest.getCode() + ", exception ocurred: " + e.getMessage());
		}

		//Fail when there is an error sending the refund request

		return Transition.NOK;



	}
}
