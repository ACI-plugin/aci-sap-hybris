/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hybris.aci.dao.ACIAccountDao;


/**
 *
 */
public class DefaultACIAccountDao extends AbstractItemDao implements ACIAccountDao
{
	private static final String FIND_SAVED_PAYMENT_INFOS_BY_CUSTOMER_QUERY = "SELECT {" + PaymentInfoModel.PK + "} FROM {"
			+ PaymentInfoModel._TYPECODE + "} WHERE {" + PaymentInfoModel.USER + "} = ?customer AND {" + PaymentInfoModel.SAVED
			+ "} = ?saved AND {" + PaymentInfoModel.DUPLICATE + "} = ?duplicate";


	/**
	 * Gets ACI Payment informations
	 *
	 * @param customerModel
	 * @return
	 */
	@Override
	public List<PaymentInfoModel> getAciCardPaymentInfos(final CustomerModel customerModel)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("saved", Boolean.TRUE);
		queryParams.put("duplicate", Boolean.FALSE);
		final SearchResult<PaymentInfoModel> result = getFlexibleSearchService().search(FIND_SAVED_PAYMENT_INFOS_BY_CUSTOMER_QUERY,
				queryParams);
		return result.getResult();
	}

}
