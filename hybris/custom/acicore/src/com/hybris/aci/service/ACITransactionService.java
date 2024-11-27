/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;


/**
 * ACI Transaction Service Interface
 */
public interface ACITransactionService
{
	/**
	 * @param aciAuthorizationResponse
	 * @param abstractOrderModel
	 */
	void saveTransactionResponse(final ACIPaymentProcessResponse aciAuthorizationResponse,
			final AbstractOrderModel abstractOrderModel);

	/**
	 * @param aciAuthorizationResponse
	 * @param abstractOrderModel
	 * @return PaymentTransactionModel
	 */
	PaymentTransactionModel createPaymentTransaction(ACIPaymentProcessResponse aciAuthorizationResponse,
			AbstractOrderModel abstractOrderModel);

	/**
	 * @param paymentTransaction
	 * @param aciPaymentProcessResponse
	 * @return PaymentTransactionEntryModel
	 */
	PaymentTransactionEntryModel createPaymentTransactionEntryModel(PaymentTransactionModel paymentTransaction,
			ACIPaymentProcessResponse aciPaymentProcessResponse);

}
