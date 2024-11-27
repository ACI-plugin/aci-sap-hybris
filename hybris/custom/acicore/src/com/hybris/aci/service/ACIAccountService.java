/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;

import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;


/**
 * ACI Account Service Interface
 */
public interface ACIAccountService
{
	/**
	 * @param currentCustomer
	 * @return List<PaymentInfoModel>
	 */
	List<PaymentInfoModel> getAciCardPaymentInfos(final CustomerModel currentCustomer);

	/**
	 * @param response
	 * @param customerModel
	 */
	void createAciPaymentInfo(ACIPaymentProcessResponse response, CustomerModel customerModel);

	/**
	 * @param currentCustomer
	 * @param aciCardPaymentInfo
	 */
	void unlinkACIPaymentInfo(CustomerModel currentCustomer, PaymentInfoModel aciCardPaymentInfo);
}
