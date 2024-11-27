/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import static de.hybris.platform.core.enums.OrderStatus.CANCELLED;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.service.ACIPaymentService;


/**
 * Used for cancellations by ImmediateCancelRequestExecutor
 */
public class ACIOrderCancelPaymentServiceAdapter implements OrderCancelPaymentServiceAdapter
{
	private static final Logger LOG = Logger.getLogger(ACIOrderCancelPaymentServiceAdapter.class);
	private static final Object PAYMENT_PROVIDER = "ACI";

	private ACIPaymentService aciPaymentService;
	private ModelService modelService;
	private CalculationService calculationService;

	/**
	 * Issues a cancel request for complete cancelled orders
	 *
	 * @param order
	 */
	@Override
	public void recalculateOrderAndModifyPayments(final OrderModel order)
	{
		LOG.debug("recalculateOrderAndModifyPayments received for order: " + order.getCode() + ":" + order.getTotalPrice() + ":"
				+ order.getStatus().getCode());
		final Double oldPrice = order.getTotalPrice();
		LOG.info("old amount: " + oldPrice);
		try
		{
			calculationService.recalculate(order);
		}
		catch (final CalculationException e)
		{
			LOG.error(e);
		}
		boolean isPartialCancel = false;
		final Double newPrice = order.getTotalPrice();
		LOG.info("new amount: " + newPrice);

		//Send the cancel request only when the whole order is cancelled
		if (!CANCELLED.getCode().equals(order.getStatus().getCode()))
		{
			LOG.info("Partial cancellation - do nothing");
			LOG.info("Order Total Price :: " + order.getTotalPrice());
			isPartialCancel = true;
		}

		if (order.getPaymentTransactions().isEmpty())
		{
			LOG.warn("No transaction found!");
			return;
		}
		final PaymentTransactionModel transaction = order.getPaymentTransactions().get(0);

		//Ignore non-ACI payments
		if (!PAYMENT_PROVIDER.equals(transaction.getPaymentProvider()))
		{
			LOG.debug("Different Payment provider: " + transaction.getPaymentProvider());
			return;
		}

		if (transaction.getEntries().isEmpty())
		{
			LOG.warn("Cannot find auth transaction!");
			return;
		}
		final PaymentTransactionEntryModel transactionEntry = transaction.getEntries().get(0);

		if (isPartialCancel)
		{
			if (transactionEntry.getType().equals(PaymentTransactionType.CAPTURE))
			{

				final double reversalAmount = order.getTotalPrice().doubleValue() - transactionEntry.getAmount().doubleValue();
				final ACIPaymentProcessResponse aciPaymentProcessResponse = aciPaymentService.cancel(order, transactionEntry,
						String.format("%.2f", Double.valueOf(reversalAmount)), transactionEntry.getCurrency().getIsocode());

				LOG.info("Saving transaction " + aciPaymentProcessResponse.getId() + ":"
						+ aciPaymentProcessResponse.getResult().getDescription());
			}
			else
			{
				LOG.info("Partial cancellation - do nothing - Partial reversal not supported");

			}
		}
		else
		{
			final ACIPaymentProcessResponse aciPaymentProcessResponse = aciPaymentService.cancel(order, transactionEntry,
					String.format("%.2f", transactionEntry.getAmount()), transactionEntry.getCurrency().getIsocode());

			LOG.info("Saving transaction " + aciPaymentProcessResponse.getId() + ":"
					+ aciPaymentProcessResponse.getResult().getDescription());
		}
	}

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


	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}
}
