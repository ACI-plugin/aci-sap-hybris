/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * ACI Business Process Service Interface
 */
public interface ACIBusinessProcessService
{

	/**
	 * @param orderModel
	 * @param event
	 */
	void triggerOrderProcessEvent(OrderModel orderModel, String event);

	/**
	 * @param orderModel
	 * @param event
	 */
	void triggerReturnProcessEvent(OrderModel orderModel, String event);
}
