/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;


/**
 * ACIOrderService Interface
 */
public interface ACIOrderService
{
	/**
	 * @param paymentId
	 * @return OrderModel
	 */
	OrderModel findOrderByAciPaymentId(String paymentId);

	/**
	 *
	 * @return List<OrderModel>
	 */
	List<OrderModel> findPendingOrders();

	/**
	 * @param code
	 * @return OrderModel
	 */
	OrderModel findOrderModel(final String code);

}
