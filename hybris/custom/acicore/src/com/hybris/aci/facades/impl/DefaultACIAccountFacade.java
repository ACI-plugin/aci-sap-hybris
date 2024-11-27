/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aci.payment.data.ACIConfigData;
import com.aci.payment.data.ACIPaymentInfoData;
import com.hybris.aci.facades.ACIAccountFacade;
import com.hybris.aci.facades.ACIConfigFacade;
import com.hybris.aci.facades.ACIPaymentFacade;
import com.hybris.aci.httpclient.ACITokenization;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.populator.DataPopulator;
import com.hybris.aci.service.ACIAccountService;
import com.hybris.aci.util.LogHelper;


public class DefaultACIAccountFacade implements ACIAccountFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultACIAccountFacade.class);

	private static final String SUCCESS = "SUCCESS";

	private static final String FAILURE = "FAILURE";

	UserService userService;
	ACIAccountService aciAccountService;
	ACIPaymentFacade aciPaymentFacade;
	ACIConfigFacade aciConfigFacade;
	Converter<PaymentInfoModel, ACIPaymentInfoData> aciPaymentInfoConverter;


	/**
	 * Get all payment infos for the user
	 *
	 * @return
	 */
	@Override
	public List<ACIPaymentInfoData> getACIPaymentInfos()
	{
		LogHelper.debugLog(LOG, "DefaultACIAccountFacade getACIPaymentInfos - Start");
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final List<PaymentInfoModel> aciCards = getAciAccountService().getAciCardPaymentInfos(currentCustomer);
		final List<ACIPaymentInfoData> aciPaymentInfos = new ArrayList<ACIPaymentInfoData>();
		final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();


		for (final PaymentInfoModel aciPaymentInfoModel : aciCards)
		{
			final ACIPaymentInfoData paymentInfoData = getAciPaymentInfoConverter().convert(aciPaymentInfoModel);
			if (aciPaymentInfoModel.equals(defaultPaymentInfoModel))
			{
				//paymentInfoData.setDefaultPaymentInfo(true);
				aciPaymentInfos.add(0, paymentInfoData);
			}
			else
			{
				aciPaymentInfos.add(paymentInfoData);
			}
		}
		LOG.info("Total payments available - " + aciPaymentInfos.size());
		return aciPaymentInfos;

	}

	/**
	 * Creates payment infos
	 *
	 * @param resourcePath
	 */
	@Override
	public void createAciPaymentInfo(final String resourcePath)
	{
		LogHelper.debugLog(LOG, "DefaultACIAccountFacade createAciPaymentInfo - Start");

		final com.hybris.aci.httpclient.models.ACIPaymentProcessResponse response = aciPaymentFacade.getAciStatus(resourcePath);
		final CustomerModel customerModel = (CustomerModel) getUserService().getCurrentUser();
		getAciAccountService().createAciPaymentInfo(response, customerModel);
	}

	/**
	 * Detaches the payment info from customer on payment info delete in my account
	 *
	 * @param id
	 * @return
	 */
	@Override
	public String unlinkACIPaymentInfo(final String id)
	{
		LogHelper.debugLog(LOG, "DefaultACIAccountFacade unlinkACIPaymentInfo - Start");
		validateParameterNotNullStandardMessage("id", id);
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		for (final PaymentInfoModel aciCardPaymentInfo : aciAccountService.getAciCardPaymentInfos(currentCustomer))
		{
			if (aciCardPaymentInfo.getPk().toString().equals(id))
			{
				final ACIConfigData aciconfig = aciConfigFacade.getACIConfig();
				final DataPopulator dp = new DataPopulator();
				final Map<String, String> m = new HashMap<String, String>();
				dp.populateAuthData(m, aciconfig.getEntityId());
				final ACITokenization aciTokenization = new ACITokenization(aciConfigFacade.getBaseUrl(),
						aciConfigFacade.getAPiVersion());

				final ACIPaymentProcessResponse response = aciTokenization.deleteAcitoken(m, aciconfig.getBearerToken(),
						aciCardPaymentInfo.getAciRegistrationId());
				if (response.isOk())
				{
					aciAccountService.unlinkACIPaymentInfo(currentCustomer, aciCardPaymentInfo);
					LogHelper.debugLog(LOG, "DefaultACIAccountFacade unlinkACIPaymentInfo - success");
					return SUCCESS;
				}
				else
				{
					LOG.error("ACI Delete token failed : " + response.getResult().getCode() + " - "
							+ response.getResult().getDescription());
					return FAILURE;
				}

			}
		}
		return SUCCESS;
		//updateDefaultPaymentInfo(currentCustomer);
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the aciAccountService
	 */
	public ACIAccountService getAciAccountService()
	{
		return aciAccountService;
	}

	/**
	 * @param aciAccountService
	 *           the aciAccountService to set
	 */
	public void setAciAccountService(final ACIAccountService aciAccountService)
	{
		this.aciAccountService = aciAccountService;
	}

	/**
	 * @return the acipaymentFacade
	 */
	public ACIPaymentFacade getAciPaymentFacade()
	{
		return aciPaymentFacade;
	}

	/**
	 * @param aciPaymentFacade
	 *           the acipaymentFacade to set
	 */
	public void setAciPaymentFacade(final ACIPaymentFacade aciPaymentFacade)
	{
		this.aciPaymentFacade = aciPaymentFacade;
	}

	/**
	 * @return the aciPaymentInfoConverter
	 */
	public Converter<PaymentInfoModel, ACIPaymentInfoData> getAciPaymentInfoConverter()
	{
		return aciPaymentInfoConverter;
	}

	/**
	 * @param aciPaymentInfoConverter
	 *           the aciPaymentInfoConverter to set
	 */
	public void setAciPaymentInfoConverter(final Converter<PaymentInfoModel, ACIPaymentInfoData> aciPaymentInfoConverter)
	{
		this.aciPaymentInfoConverter = aciPaymentInfoConverter;
	}

	/**
	 * @return the aciConfigFacade
	 */
	public ACIConfigFacade getAciConfigFacade()
	{
		return aciConfigFacade;
	}

	/**
	 * @param aciConfigFacade
	 *           the aciConfigFacade to set
	 */
	public void setAciConfigFacade(final ACIConfigFacade aciConfigFacade)
	{
		this.aciConfigFacade = aciConfigFacade;
	}

}
