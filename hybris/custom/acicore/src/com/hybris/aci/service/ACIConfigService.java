/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import com.aci.model.ACIConfigModel;


/**
 * ACI Config Service Interface
 */
public interface ACIConfigService
{
	/**
	 *
	 * @return String
	 */
	String getBaseUrl();

	/**
	 *
	 * @return String
	 */
	String getAPiVersion();

	/**
	 *
	 * @return ACIConfigModel
	 */
	ACIConfigModel getACIConfig();

	/**
	 *
	 * @return String
	 */
	String getAPiVersionFromConfig();

	/**
	 *
	 * @return String
	 */
	String getBaseUrlFromConfig();
}
