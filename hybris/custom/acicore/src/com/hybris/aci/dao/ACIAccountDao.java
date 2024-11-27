/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.dao;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;


public interface ACIAccountDao
{
	List<PaymentInfoModel> getAciCardPaymentInfos(final CustomerModel currentCustomer);
}
