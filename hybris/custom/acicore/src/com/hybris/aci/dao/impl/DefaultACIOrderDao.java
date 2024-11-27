/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.dao.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hybris.aci.dao.ACIOrderDao;


public class DefaultACIOrderDao implements ACIOrderDao
{
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Find orfer for the aci payment id
	 *
	 * @param paymentId
	 * @return
	 */
	@Override
	public List<OrderModel> findOrderByAciPaymentId(final String paymentId)
	{
		final String queryString = "SELECT {p:" + OrderModel.PK + "}" + "FROM {" + OrderModel._TYPECODE + " AS p} " + "WHERE "
				+ "{p:" + OrderModel.ACIPAYMENTID + "}=?id ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("id", paymentId);

		return flexibleSearchService.<OrderModel> search(query).getResult();
	}

	/**
	 * Find the ACI pending orders
	 *
	 * @return
	 */
	@Override
	public List<OrderModel> findPendingOrders()
	{
		final String queryString = "SELECT {p:" + OrderModel.PK + "}" + "FROM {" + OrderModel._TYPECODE + " AS p} " + "WHERE "
				+ "{p:" + OrderModel.STATUS + "}=?status ";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("status", OrderStatus.WAITING_ACI_PAYMENT);

		return flexibleSearchService.<OrderModel> search(query).getResult();
	}

	@Override
	public OrderModel findOrderModel(final String code)
	{
		final Map queryParams = new HashMap();
		queryParams.put("code", code);

		//Adding "{versionID} IS NULL" to get the original order regardless of modification history
		final FlexibleSearchQuery selectOrderQuery = new FlexibleSearchQuery("SELECT {pk} FROM {" + OrderModel._TYPECODE + "}"
				+ " WHERE {" + OrderModel.CODE + "} = ?code" + " AND {versionID} IS NULL", queryParams);



		return (OrderModel) flexibleSearchService.searchUnique(selectOrderQuery);
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
