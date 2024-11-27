/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import org.apache.log4j.Logger;

import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.service.ACIPaymentService;
import com.hybris.aci.util.LogHelper;


/**
 * The TakePayment step captures the payment transaction.
 */
public class ACITakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(ACITakePaymentAction.class);

	private ACIPaymentService aciPaymentService;

	/**
	 * @return the aciPaymentService
	 */
	public ACIPaymentService getAciPaymentService()
	{
		return aciPaymentService;
	}

	/**
	 * @param aciPaymentService
	 *           the aciPaymentService to set
	 */
	public void setAciPaymentService(final ACIPaymentService aciPaymentService)
	{
		this.aciPaymentService = aciPaymentService;
	}

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();


		LogHelper.debugLog(LOG, "ACI TakePayment Action Execution Started");
		LogHelper.debugLog(LOG, "ACI Payment type - " + order.getAciPaymentType());
		if (order.getAciPaymentType().equals(ACIUtilConstants.PREAUTHORIZATION_CODE))
		{
			LogHelper.debugLog(LOG, "ACI TakePayment Action - Going for Capture call");

			final ACIPaymentProcessResponse response = aciPaymentService.capture(order);

			if (!response.isOk())
			{
				LOG.info("ACI TakePayment Action - Capture Failed.");
				order.setStatus(OrderStatus.PAYMENT_NOT_CAPTURED);
				order.setPaymentStatus(PaymentStatus.NOTPAID);
				return Transition.NOK;
			}
			else
			{
				order.setStatus(OrderStatus.PAYMENT_CAPTURED);
				order.setPaymentStatus(PaymentStatus.PAID);
			}
			modelService.save(order);
			modelService.refresh(order);
			LOG.info("ACI TakePayment Action - Capture Success.");
		}

		return Transition.OK;
	}

}
