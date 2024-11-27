/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.executors;

import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.impl.executors.ImmediateCancelRequestExecutor;

import java.util.logging.Logger;


public class ACIImmediateCancelRequestExecutor extends ImmediateCancelRequestExecutor
{
	private static final Logger LOG = Logger.getLogger(ACIImmediateCancelRequestExecutor.class.getName());

	@Override
	protected void updateOrderProcess(final OrderCancelRequest orderCancelRequest)
	{
		LOG.info("This is a sample integration to execute ACI Payment cancellation. Please provide your own implementation");
	}
}
