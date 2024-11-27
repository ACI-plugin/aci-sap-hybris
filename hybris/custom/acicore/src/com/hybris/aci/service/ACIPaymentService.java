/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;


/**
 * ACI Payment Service Interface
 */
public interface ACIPaymentService
{

	/**
	 * @param order
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse capture(AbstractOrderModel order);

	/**
	 * @param resourcePath
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse getAciStatus(String resourcePath);

	/**
	 * @param order
	 * @param transactionEntry
	 * @param amount
	 * @param currency
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse cancel(OrderModel order, PaymentTransactionEntryModel transactionEntry, final String amount,
			final String currency);

	/**
	 * @param order
	 * @param transactionEntry
	 * @param amount
	 * @param currency
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse refund(final AbstractOrderModel order, final PaymentTransactionEntryModel transactionEntry,
			final String amount, final String currency);

	/**
	 * @param order
	 * @param transactionEntry
	 * @param amount
	 * @param currency
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse reversal(final AbstractOrderModel order, final PaymentTransactionEntryModel transactionEntry,
			final String amount, final String currency);

	/**
	 * @param order
	 * @param amount
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse partialCapture(AbstractOrderModel order, String amount);

	/**
	 * @param order
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse getPaymentStatus(OrderModel order);

	/**
	 * @param order
	 * @param transaction
	 * @param amount
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse refund(AbstractOrderModel order, PaymentTransactionModel transaction, String amount);
}
