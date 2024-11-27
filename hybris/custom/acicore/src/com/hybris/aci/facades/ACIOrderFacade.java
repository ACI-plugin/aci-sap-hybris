/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades;

import de.hybris.platform.commercefacades.order.data.OrderData;


/**
 * ACI Order Facade Interface
 */
public interface ACIOrderFacade
{
	/**
	 * @param paymentId
	 * @return OrderData
	 */
	OrderData getOrderByAciPaymentId(String paymentId);
}
