/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;

import com.hybris.aci.actions.AbstractWaitableAction;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.util.LogHelper;


/**
 * This action implements payment authorization using {@link CreditCardPaymentInfoModel}. Any other payment model could
 * be implemented here, or in a separate action, if the process flow differs.
 */
public class ACICheckAuthorizeOrderPaymentAction extends AbstractWaitableAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(ACITakePaymentAction.class);

	public Transition executeAction(final OrderProcessModel process) throws RetryLaterException, Exception
	{
		LogHelper.debugLog(LOG, "ACICheckAuthorizeOrderPaymentAction - Start");
		final OrderModel order = process.getOrder();

		if (order != null)
		{
			if (order.getPaymentInfo() instanceof InvoicePaymentInfoModel)
			{
				return Transition.AUTHOK;
			}
			else
			{
				return assignStatusForOrder(order);
			}
		}
		return Transition.AUTHNOK;
	}

	/**
	 * Sets the status for given order in case on of its {@link PaymentTransactionEntryModel} matches proper
	 * {@link PaymentTransactionType} and {@link TransactionStatus}.
	 *
	 * @param order
	 *           {@link OrderModel}
	 * @return {@link Transition}
	 */
	protected Transition assignStatusForOrder(final OrderModel order)
	{
		Transition decision = Transition.AUTHNOK;
		LogHelper.debugLog(LOG, "ACICheckAuthorizeOrderPaymentAction -  assignStatusForOrder - Start");

		for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
		{
			for (final PaymentTransactionEntryModel entry : transaction.getEntries())
			{
				if (order.getAciPaymentType().equals(ACIUtilConstants.PREAUTHORIZATION_CODE))
				{
					decision = assignStatusForPAOrder(order, entry);
				}
				else
				{
					decision = assignStatusForDBOrder(order, entry);
				}

				modelService.save(order);
			}
		}
		LOG.info("ACICheckAuthorizeOrderPaymentAction decision - " + decision);

		return decision;
	}

	protected Transition assignStatusForPAOrder(final OrderModel order, final PaymentTransactionEntryModel entry)
	{
		LogHelper.debugLog(LOG, "ACI Order Payment Type - PA ");
		Transition decision = Transition.AUTHNOK;

		final String transactionStatus = entry.getTransactionStatus();
		if (entry.getType().equals(PaymentTransactionType.AUTHORIZATION))
		{
			if (TransactionStatus.ACCEPTED.name().equals(transactionStatus))
			{
				order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
				decision = Transition.AUTHOK;

			}
			if (TransactionStatus.PENDING.name().equals(transactionStatus))
			{
				order.setStatus(OrderStatus.WAITING_ACI_PAYMENT);
				decision = Transition.AUTHWAIT;
			}
			if (TransactionStatus.REJECTED.name().equals(transactionStatus))
			{
				order.setStatus(OrderStatus.CANCELLED);
			}
		}
		modelService.save(order);

		LogHelper.debugLog(LOG, "assignStatusForPAOrder  decision :" + decision);
		LogHelper.debugLog(LOG, "assignStatusForPAOrder  order status :" + order.getStatus());

		return decision;
	}

	protected Transition assignStatusForDBOrder(final OrderModel order, final PaymentTransactionEntryModel entry)
	{
		LogHelper.debugLog(LOG, "ACI Order Payment Type - DB ");

		Transition decision = Transition.SALENOK;
		if (entry.getType().equals(PaymentTransactionType.CAPTURE)
				&& TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus()))
		{
			order.setStatus(OrderStatus.PAYMENT_CAPTURED);
			order.setPaymentStatus(PaymentStatus.PAID);
			decision = Transition.SALEOK;
		}
		if (entry.getType().equals(PaymentTransactionType.CAPTURE)
				&& TransactionStatus.REJECTED.name().equals(entry.getTransactionStatus()))
		{
			order.setStatus(OrderStatus.CANCELLED);
			order.setPaymentStatus(PaymentStatus.NOTPAID);

		}

		modelService.save(order);

		LogHelper.debugLog(LOG, "assignStatusForDBOrder  decision :" + decision);
		LogHelper.debugLog(LOG, "assignStatusForDBOrder  order status :" + order.getStatus());

		return decision;
	}

	@Override
	public String execute(final OrderProcessModel orderProcessModel) throws RetryLaterException, Exception
	{
		return executeAction(orderProcessModel).toString();
	}
}
