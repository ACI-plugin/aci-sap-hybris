/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.apache.log4j.Logger;

import com.aci.model.ACIConfigModel;
import com.aci.payment.data.ACIConfigData;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.facades.ACIConfigFacade;
import com.hybris.aci.service.ACIConfigService;


public class DefaultACIConfigFacade implements ACIConfigFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultACIConfigFacade.class);

	private Converter aciPaymentConfigConverter;
	private SiteConfigService siteConfigService;
	private ACIConfigService aciConfigService;


	/**
	 * Gets the Script URL from the properties
	 *
	 * @param checkoutId
	 * @return
	 */
	@Override
	public String getACIScriptUrl(final String checkoutId)
	{
		final String version = siteConfigService.getProperty(ACIUtilConstants.ACI_VERSION);
		final String mode = siteConfigService.getProperty(ACIUtilConstants.ACI_MODE);
		LOG.info("ACI mode of operations - " + mode);
		final StringBuffer sb = new StringBuffer();
		String baseUrl = "";
		if (mode.equalsIgnoreCase(ACIUtilConstants.ACI_LIVE))
		{
			baseUrl = siteConfigService.getProperty(ACIUtilConstants.ACI_BASEURL);

		}
		else
		{
			baseUrl = siteConfigService.getProperty(ACIUtilConstants.ACI_TEST_BASEURL);
		}
		final String jsPath = siteConfigService.getProperty(ACIUtilConstants.ACI_SCRIPT_PATH);
		return sb.append(baseUrl).append(ACIUtilConstants.SLASH).append(version).append(jsPath).append(checkoutId).toString();

	}


	/**
	 * Gets the configuration details
	 *
	 * @return
	 */
	@Override
	public ACIConfigData getACIConfig()
	{
		ACIConfigData aciConfigData = null;

		final ACIConfigModel aciConfigModel = aciConfigService.getACIConfig();
		if (aciConfigModel != null)
		{
			aciConfigData = (ACIConfigData) aciPaymentConfigConverter.convert(aciConfigModel);

		}
		return aciConfigData;
	}

	@Override
	public String getMerchantUrl(final String url)
	{
		return siteConfigService.getProperty(url);
	}

	/**
	 * @return the aciConfigService
	 */
	public ACIConfigService getAciConfigService()
	{
		return aciConfigService;
	}

	/**
	 * @param aciConfigService
	 *           the aciConfigService to set
	 */
	public void setAciConfigService(final ACIConfigService aciConfigService)
	{
		this.aciConfigService = aciConfigService;
	}

	/**
	 * @return the aciPaymentConfigConverter
	 */
	public Converter getAciPaymentConfigConverter()
	{
		return aciPaymentConfigConverter;
	}

	/**
	 * @param aciPaymentConfigConverter
	 *           the aciPaymentConfigConverter to set
	 */
	public void setAciPaymentConfigConverter(final Converter aciPaymentConfigConverter)
	{
		this.aciPaymentConfigConverter = aciPaymentConfigConverter;
	}

	/**
	 * @return the siteConfigService
	 */
	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	/**
	 * @param siteConfigService
	 *           the siteConfigService to set
	 */
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	@Override
	public String getBaseUrl()
	{
		return aciConfigService.getBaseUrl();

	}

	@Override
	public String getAPiVersion()
	{
		return aciConfigService.getAPiVersion();
	}


}
