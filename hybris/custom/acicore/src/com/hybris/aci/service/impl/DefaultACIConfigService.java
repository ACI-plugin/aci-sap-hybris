/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import org.springframework.beans.factory.annotation.Required;

import com.aci.model.ACIConfigModel;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.service.ACIConfigService;


public class DefaultACIConfigService implements ACIConfigService
{
	private SiteConfigService siteConfigService;
	BaseStoreService baseStoreService;
	private ConfigurationService configurationService;
	private UiExperienceService uiExperienceService;


	/**
	 * Gets the base url
	 *
	 * @return
	 */
	@Override
	public String getBaseUrl()
	{
		final String mode = siteConfigService.getProperty(ACIUtilConstants.ACI_MODE);

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
		return sb.append(baseUrl).toString();
	}

	/**
	 * Gets API version
	 *
	 * @return
	 */
	@Override
	public String getAPiVersion()
	{
		return siteConfigService.getProperty(ACIUtilConstants.ACI_VERSION);
	}

	/**
	 * Gets ACI Config model
	 *
	 * @return
	 */
	@Override
	public ACIConfigModel getACIConfig()
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();

		final ACIConfigModel aciConfigModel = baseStore.getACIConfig();
		return aciConfigModel;
	}

	/**
	 * Gets nbase url from configurations
	 *
	 * @return
	 */
	@Override
	public String getBaseUrlFromConfig()
	{
		final String mode = getConfigProperty(ACIUtilConstants.ACI_MODE);

		final StringBuffer sb = new StringBuffer();
		String baseUrl = "";
		if (mode.equalsIgnoreCase(ACIUtilConstants.ACI_LIVE))
		{
			baseUrl = getConfigProperty(ACIUtilConstants.ACI_BASEURL);
		}
		else
		{
			baseUrl = getConfigProperty(ACIUtilConstants.ACI_TEST_BASEURL);
		}
		return sb.append(baseUrl).toString();
	}

	/**
	 * Gets API version
	 *
	 * @return
	 */
	@Override
	public String getAPiVersionFromConfig()
	{
		return getConfigProperty(ACIUtilConstants.ACI_VERSION);
	}

	private String getConfigProperty(final String property)
	{
		return Config.getParameter(property);

	}

	/**
	 * @return the uiExperienceService
	 */
	public UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	/**
	 * @param uiExperienceService
	 *           the uiExperienceService to set
	 */
	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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

}
