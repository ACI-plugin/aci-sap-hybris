/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.dao.ACIAccountDao;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.httpclient.models.Card;
import com.hybris.aci.service.ACIAccountService;


public class DefaultACIAccountService implements ACIAccountService
{
	private static final Logger LOG = Logger.getLogger(DefaultACIAccountService.class);

	ACIAccountDao aciAccountDao;
	private ModelService modelService;


	/**
	 * Creates payment info
	 *
	 * @param response
	 * @param customerModel
	 */
	@Override
	public void createAciPaymentInfo(final ACIPaymentProcessResponse response, final CustomerModel customerModel)
	{

		final Card card = response.getCard();
		final PaymentInfoModel paymentInfoModel = getModelService().create(PaymentInfoModel.class);
		paymentInfoModel.setCode(customerModel.getUid() + "_" + UUID.randomUUID());
		paymentInfoModel.setUser(customerModel);
		paymentInfoModel.setAciRegistrationId(response.getId());
		paymentInfoModel.setAciCardNumber(ACIUtilConstants.MASK + card.getLast4Digits());
		paymentInfoModel.setAciCardBin(card.getBin());
		paymentInfoModel.setAciCardExpiryMonth(card.getExpiryMonth());
		paymentInfoModel.setAciCardExpiryYear(card.getExpiryYear());
		paymentInfoModel.setAciCardHolder(card.getHolder());
		paymentInfoModel.setAciPaymentBrand(response.getPaymentBrand());
		paymentInfoModel.setSaved(true);
		getModelService().saveAll();
		getModelService().refresh(customerModel);
	}

	/**
	 * Unlink payment info from account
	 *
	 * @param customerModel
	 * @param aciCardPaymentInfo
	 */
	@Override
	public void unlinkACIPaymentInfo(final CustomerModel customerModel, final PaymentInfoModel aciCardPaymentInfo)
	{
		validateParameterNotNull(customerModel, "Customer model cannot be null");
		validateParameterNotNull(aciCardPaymentInfo, "CreditCardPaymentInfo model cannot be null");
		if (customerModel.getPaymentInfos().contains(aciCardPaymentInfo))
		{
			final Collection<PaymentInfoModel> paymentInfoList = new ArrayList(customerModel.getPaymentInfos());
			paymentInfoList.remove(aciCardPaymentInfo);
			customerModel.setPaymentInfos(paymentInfoList);
			getModelService().save(customerModel);
			getModelService().refresh(customerModel);
		}
		else
		{
			LOG.error("ACI Card Payment Info " + aciCardPaymentInfo + " does not belong to the customer " + customerModel
					+ " and will not be removed.");
			throw new IllegalArgumentException("ACI Card Payment Info " + aciCardPaymentInfo + " does not belong to the customer "
					+ customerModel + " and will not be removed.");
		}
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the aciAccountDao
	 */
	public ACIAccountDao getAciAccountDao()
	{
		return aciAccountDao;
	}

	/**
	 * @param aciAccountDao
	 *           the aciAccountDao to set
	 */
	public void setAciAccountDao(final ACIAccountDao aciAccountDao)
	{
		this.aciAccountDao = aciAccountDao;
	}

	/**
	 * Gets aci payment infos for the current customer
	 *
	 * @param currentCustomer
	 * @return
	 */
	@Override
	public List<PaymentInfoModel> getAciCardPaymentInfos(final CustomerModel currentCustomer)
	{
		// YTODO Auto-generated method stub
		return aciAccountDao.getAciCardPaymentInfos(currentCustomer);
	}

}
