/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;

import com.hybris.aci.dao.ACIOrderDao;
import com.hybris.aci.service.ACIOrderService;


public class DefaultACIOrderService implements ACIOrderService
{
	ACIOrderDao aciOrderDao;

	/**
	 * This method does find order for aci payment id
	 *
	 * @param paymentId
	 * @return
	 */
	@Override
	public OrderModel findOrderByAciPaymentId(final String paymentId)
	{
		final List<OrderModel> orders = aciOrderDao.findOrderByAciPaymentId(paymentId);
		if (!orders.isEmpty())
		{
			return orders.get(0);
		}
		return null;
	}

	/**
	 * Gets order for given code
	 *
	 * @param code
	 * @return
	 */
	@Override
	public OrderModel findOrderModel(final String code)
	{
		return aciOrderDao.findOrderModel(code);
	}

	/**
	 * Finds the pending orders
	 *
	 * @return
	 */
	@Override
	public List<OrderModel> findPendingOrders()
	{
		return aciOrderDao.findPendingOrders();

	}

	/**
	 * @return the aciOrderDao
	 */
	public ACIOrderDao getAciOrderDao()
	{
		return aciOrderDao;
	}

	/**
	 * @param aciOrderDao
	 *           the aciOrderDao to set
	 */
	public void setAciOrderDao(final ACIOrderDao aciOrderDao)
	{
		this.aciOrderDao = aciOrderDao;
	}

}
