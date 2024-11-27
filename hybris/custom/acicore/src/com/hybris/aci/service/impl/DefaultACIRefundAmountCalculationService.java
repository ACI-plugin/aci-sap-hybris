/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;


import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.math.BigDecimal;

import com.hybris.aci.service.ACIRefundAmountCalculationService;


/**
 * This is sample class for testing Refund. Kindly implement your own logic
 */
public class DefaultACIRefundAmountCalculationService implements ACIRefundAmountCalculationService
{
	/**
	 *
	 * @param returnRequest
	 * @return
	 */
	public BigDecimal getCustomRefundAmount(final ReturnRequestModel returnRequest)
	{
		ServicesUtil.validateParameterNotNull(returnRequest, "Parameter returnRequest cannot be null");
		ServicesUtil.validateParameterNotNull(returnRequest.getReturnEntries(), "Parameter Return Entries cannot be null");
		BigDecimal refundAmount = returnRequest.getReturnEntries().stream().map((returnEntry) -> {
			return this.getCustomRefundEntryAmount(returnEntry);
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (returnRequest.getRefundDeliveryCost())
		{
			refundAmount = refundAmount.add(new BigDecimal(returnRequest.getOrder().getDeliveryCost()));
		}

		return refundAmount.setScale(this.getNumberOfDigits(returnRequest), 2);
	}

	/**
	 *
	 * @param returnEntryModel
	 * @return
	 */
	public BigDecimal getCustomRefundEntryAmount(final ReturnEntryModel returnEntryModel)
	{
		ServicesUtil.validateParameterNotNull(returnEntryModel, "Parameter Return Entry cannot be null");
		BigDecimal itemValue = new BigDecimal(0);
		if (returnEntryModel instanceof RefundEntryModel)
		{
			itemValue = this.getOriginalRefundEntryAmount(returnEntryModel);
		}

		return itemValue;
	}

	/**
	 *
	 * @param returnRequest
	 * @return
	 */
	public BigDecimal getOriginalRefundAmount(final ReturnRequestModel returnRequest)
	{
		ServicesUtil.validateParameterNotNull(returnRequest, "Parameter returnRequest cannot be null");
		ServicesUtil.validateParameterNotNull(returnRequest.getReturnEntries(), "Parameter Return Entries cannot be null");
		BigDecimal refundAmount = returnRequest.getReturnEntries().stream().map((returnEntry) -> {
			return this.getOriginalRefundEntryAmount(returnEntry);
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (returnRequest.getRefundDeliveryCost())
		{
			refundAmount = refundAmount.add(new BigDecimal(returnRequest.getOrder().getDeliveryCost()));
		}

		return refundAmount.setScale(this.getNumberOfDigits(returnRequest), 2);
	}

	/**
	 *
	 * @param returnEntryModel
	 * @return
	 */
	public BigDecimal getOriginalRefundEntryAmount(final ReturnEntryModel returnEntryModel)
	{
		ServicesUtil.validateParameterNotNull(returnEntryModel, "Parameter Return Entry cannot be null");
		final ReturnRequestModel returnRequest = returnEntryModel.getReturnRequest();
		BigDecimal refundEntryAmount = new BigDecimal(0);
		if (returnEntryModel instanceof RefundEntryModel)
		{
			final RefundEntryModel refundEntry = (RefundEntryModel) returnEntryModel;
			refundEntryAmount = refundEntry.getAmount();
			refundEntryAmount = refundEntryAmount.setScale(this.getNumberOfDigits(returnRequest), 5);
		}

		return refundEntryAmount;
	}

	protected int getNumberOfDigits(final ReturnRequestModel returnRequest)
	{
		return returnRequest.getOrder().getCurrency().getDigits();
	}
}