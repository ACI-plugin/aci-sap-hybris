/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.hybris.aci.service.ACIBusinessProcessService;


public class DefaultACIBusinessProcessService implements ACIBusinessProcessService
{
	private static final Logger LOG = Logger.getLogger(DefaultACIBusinessProcessService.class);

	private BusinessProcessService businessProcessService;

	/**
	 * Trigger order process event
	 *
	 * @param orderModel
	 * @param event
	 */
	@Override
	public void triggerOrderProcessEvent(final OrderModel orderModel, final String event)
	{
		final Collection<OrderProcessModel> orderProcesses = orderModel.getOrderProcess();
		for (final OrderProcessModel orderProcess : orderProcesses)
		{
			LOG.debug("Order process code: " + orderProcess.getCode());

			final String eventName = orderProcess.getCode() + "_" + event;
			LOG.debug("Sending event:" + eventName);
			businessProcessService.triggerEvent(eventName);
		}
	}

	/**
	 * Triggers return process event
	 *
	 * @param orderModel
	 * @param event
	 */
	@Override
	public void triggerReturnProcessEvent(final OrderModel orderModel, final String event)
	{
		final List<ReturnRequestModel> returnRequests = orderModel.getReturnRequests();
		for (final ReturnRequestModel returnRequest : returnRequests)
		{
			final Collection<ReturnProcessModel> returnProcesses = returnRequest.getReturnProcess();
			for (final ReturnProcessModel returnProcess : returnProcesses)
			{
				LOG.debug("Return process code: " + returnProcess.getCode());

				final String eventName = returnProcess.getCode() + "_" + event;
				LOG.debug("Sending event:" + eventName);
				businessProcessService.startProcess(returnProcess);
			}
		}
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}
}
