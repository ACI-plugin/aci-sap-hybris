/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades;

import de.hybris.platform.commercefacades.user.data.CustomerData;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;


/**
 * ACI Payment Facade Interface
 */
public interface ACIPaymentFacade
{

	/**
	 * @param isCheckout
	 * @param checkoutId
	 * @param ipAddress
	 * @param customerData
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse getAciCheckoutID(boolean isCheckout, final String checkoutId, final String ipAddress,
			CustomerData customerData);

	/**
	 * @param resourcePath
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse getAciStatus(String resourcePath);

	/**
	 *
	 * @return ACIPaymentProcessResponse
	 */
	ACIPaymentProcessResponse handlePlaceOrderFailure();
}
