/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades;

import com.aci.payment.data.ACIConfigData;


/**
 * ACI Config Facade Interface
 */
public interface ACIConfigFacade
{
	/**
	 *
	 * @return ACIConfigData
	 */
	ACIConfigData getACIConfig();

	/**
	 *
	 * @return String
	 */
	String getBaseUrl();

	/**
	 * @param url
	 * @return String
	 */
	String getMerchantUrl(String url);

	/**
	 * @param checkoutId
	 * @return String
	 */
	String getACIScriptUrl(String checkoutId);

	/**
	 *
	 * @return String
	 */
	String getAPiVersion();
}
