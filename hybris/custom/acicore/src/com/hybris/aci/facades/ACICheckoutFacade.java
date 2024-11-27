/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.order.InvalidCartException;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;


/**
 * ACI Checkout Facade Interface
 */
public interface ACICheckoutFacade
{

	/**
	 *
	 * @return boolean
	 */
	public boolean hasNoPaymentInfo();

	/**
	 * @param resourcePath
	 * @return ACIPaymentProcessResponse
	 */
	public ACIPaymentProcessResponse authorizeOrder(String resourcePath);

	/**
	 * @param aciAuthorizationResponse
	 * @return OrderData
	 */
	OrderData placeorder(ACIPaymentProcessResponse aciAuthorizationResponse) throws InvalidCartException;

	/**
	 * @param addressData
	 *
	 */
	void processPaymentAddress(AddressData addressData);

	/**
	 * @param sessionId
	 *
	 */
	void updateCartWithSessionId(String sessionId);


}
