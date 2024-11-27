/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.aci.controller;

import static com.hybris.aci.constants.AcicoreConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hybris.aci.facades.ACICheckoutFacade;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.service.ACIOrderService;
import com.hybris.aci.service.ACIPaymentService;
import com.hybris.aci.service.AcicoreService;


@Controller
@RequestMapping(value = "/backoffice/ops")
public class AcicoreBackOfficeController
{
	private static final String SELECTOPERATION = "selectoperation";
	private static final String STATUS = "status";
	private static final String ERROR_MSG = "Please add a lesser amount ";

	@Autowired
	private AcicoreService acicoreService;
	@Autowired
	ACIOrderService aciOrderService;
	@Autowired
	ACIPaymentService aciPaymentService;
	@Autowired
	ACICheckoutFacade aciCheckoutFacade;
	@Autowired
	ModelService modelService;

	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", acicoreService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return SELECTOPERATION;
	}

	@RequestMapping(value = "/simulator", method = RequestMethod.POST)
	public String processOp(final ModelMap model, final HttpServletRequest request,
			@RequestParam(value = "orderNo", required = false)
			final String orderNo, @RequestParam(value = "operation", required = false)
			final String operation, @RequestParam(value = "amount", required = false)
			final String amount)
	{

		model.addAttribute("logoUrl", acicoreService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		try
		{
			OrderModel order = null;
			try
			{
				order = aciOrderService.findOrderModel(orderNo);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
				model.addAttribute(STATUS, "Order not found");
				return SELECTOPERATION;
			}
			if (operation.equals("PARTIAL_CAPTURE"))
			{
				handlePartialCapture(order, amount, model);
			}

			if (operation.equals("REFUND"))
			{
				handleRefund(order, amount, model);
			}


			if (operation.equals("REVERSAL"))
			{
				handleReversal(order, amount, model);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			model.addAttribute(STATUS, operation + " - Failed");
		}

		return SELECTOPERATION;
	}

	private void handlePartialCapture(final OrderModel order, final String amount, final ModelMap model)
	{

		if (Double.parseDouble(amount) < order.getTotalPrice().intValue())
		{

			order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
			modelService.save(order);
			final ACIPaymentProcessResponse response = aciPaymentService.partialCapture(order, amount);
			if (response.isOk())
			{
				model.addAttribute(STATUS, "Operation - PARTIAL_CAPTURE successfull");
				order.setStatus(OrderStatus.PARTIAL_CAPTURED);
				order.setPaymentStatus(PaymentStatus.PARTPAID);
				modelService.save(order);
			}
			else
			{
				model.addAttribute(STATUS, response.getResult().getCode() + " - " + response.getErrorMessage());
			}

		}
		else
		{
			model.addAttribute(STATUS, ERROR_MSG);
		}

	}

	private void handleRefund(final OrderModel order, String amount, final ModelMap model)
	{

		if (amount == null)
		{
			amount = order.getTotalPrice().toString();
		}
		if (Integer.parseInt(amount) <= order.getTotalPrice().intValue())
		{

			final ACIPaymentProcessResponse response = aciPaymentService.refund(order, order.getPaymentTransactions().get(0),
					amount);
			if (response.isOk())
			{
				model.addAttribute(STATUS, "Operation - REFUND successfull");

			}
			else
			{
				model.addAttribute(STATUS, response.getResult().getCode() + " - " + response.getErrorMessage());
			}

		}
		else
		{
			model.addAttribute(STATUS, ERROR_MSG);
		}

	}

	private void handleReversal(final OrderModel order, String amount, final ModelMap model)
	{

		if (amount == null)
		{
			amount = order.getTotalPrice().toString();
		}
		if (Integer.parseInt(amount) <= order.getTotalPrice().intValue())
		{

			for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
			{

				final PaymentTransactionEntryModel transactionEntry = getTransactionEntryforReversal(transaction);

				final ACIPaymentProcessResponse response = aciPaymentService.reversal(order, transactionEntry, amount,
						order.getCurrency().getIsocode());
				if (response.isOk())
				{
					model.addAttribute(STATUS, "Operation - REVERSAL successfull");
				}
				else
				{
					model.addAttribute(STATUS, response.getResult().getCode() + " - " + response.getErrorMessage());
				}

			}
		}
		else
		{
			model.addAttribute(STATUS, ERROR_MSG);
		}

	}

	private PaymentTransactionEntryModel getTransactionEntryforReversal(final PaymentTransactionModel transaction)
	{
		PaymentTransactionEntryModel transactionEntry = null;
		final Iterator ite = transaction.getEntries().iterator();

		while (ite.hasNext())
		{
			final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) ite.next();
			if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
			{
				transactionEntry = pte;
				break;
			}
		}
		return transactionEntry;
	}
}
