/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.dao;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;


public interface ACIOrderDao
{
	List<OrderModel> findOrderByAciPaymentId(String paymentId);

	List<OrderModel> findPendingOrders();

	OrderModel findOrderModel(String code);
}
