/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.job;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import org.apache.log4j.Logger;

import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.httpclient.HttpACIClientHandler;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.service.ACIBusinessProcessService;
import com.hybris.aci.service.ACIOrderService;
import com.hybris.aci.service.ACIPaymentService;
import com.hybris.aci.service.ACITransactionService;
import com.hybris.aci.util.ACIUtil;
import com.hybris.aci.util.LogHelper;


public class ACIProcessPendingPaymentJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = Logger.getLogger(HttpACIClientHandler.class);
	private static final String LOG_MSG = "Job Processing for Order - ";
	public static final String PROCESS_EVENT_ACI_AUTHORIZED = "ACIAuthorized";

	ACIBusinessProcessService aciBusinessProcessService;

	ACIOrderService aciOrderService;

	ACIPaymentService aciPaymentService;

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

	ACITransactionService aciTransactionService;

	/**
	 * @return the aciTransactionService
	 */
	public ACITransactionService getAciTransactionService()
	{
		return aciTransactionService;
	}

	/**
	 * @param aciTransactionService
	 *           the aciTransactionService to set
	 */
	public void setAciTransactionService(final ACITransactionService aciTransactionService)
	{
		this.aciTransactionService = aciTransactionService;
	}

	/**
	 * @return the aciBusinessProcessService
	 */
	public ACIBusinessProcessService getAciBusinessProcessService()
	{
		return aciBusinessProcessService;
	}

	/**
	 * @param aciBusinessProcessService
	 *           the aciBusinessProcessService to set
	 */
	public void setAciBusinessProcessService(final ACIBusinessProcessService aciBusinessProcessService)
	{
		this.aciBusinessProcessService = aciBusinessProcessService;
	}

	/**
	 * Perform the job for handling pending payments
	 *
	 * @param arg0
	 * @return
	 */
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		final List<OrderModel> orderList = aciOrderService.findPendingOrders();
		if (!orderList.isEmpty())
		{
			for (final OrderModel order : orderList)
			{
				LogHelper.debugLog(LOG, LOG_MSG + order.getCode());
				final ACIPaymentProcessResponse aciStatusResponse = aciPaymentService.getPaymentStatus(order);
				if (aciStatusResponse != null)
				{
					handleACIResponse(aciStatusResponse, order);
				}
			}
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	private void handleACIResponse(final ACIPaymentProcessResponse aciStatusResponse, final OrderModel order)
	{

		if (aciStatusResponse.getResult() != null)
		{
			final String aciPaymentStatus = ACIUtil.processResultCode(aciStatusResponse.getResult().getCode());
			order.setAciPaymentStatus(ACIUtilConstants.aciPaymentStatusMap.get(aciPaymentStatus));
		}
		if (aciStatusResponse.isOk())
		{
			if (!ACIUtilConstants.PAYMENT_PENDING
					.equalsIgnoreCase(ACIUtil.processResultCode(aciStatusResponse.getResult().getCode())))
			{
				final PaymentTransactionModel paymentTransactionModel = order.getPaymentTransactions().get(0);
				final PaymentTransactionEntryModel authorisedTransaction = aciTransactionService
						.createPaymentTransactionEntryModel(paymentTransactionModel, aciStatusResponse);

				modelService.save(authorisedTransaction);

				modelService.refresh(paymentTransactionModel);
				aciBusinessProcessService.triggerOrderProcessEvent(order, PROCESS_EVENT_ACI_AUTHORIZED);
				LogHelper.debugLog(LOG, LOG_MSG + order.getCode() + " Payment Authorized");

			}
			else
			{
				LogHelper.debugLog(LOG, LOG_MSG + order.getCode() + " Still Payment Pending");
			}
		}
		else
		{
			order.setStatus(OrderStatus.CANCELLED);
			LogHelper.debugLog(LOG, LOG_MSG + order.getCode() + " Payment Rejected. Hence Cancelled");
		}
		modelService.save(order);


	}

	/**
	 * @return the aciOrderService
	 */
	public ACIOrderService getAciOrderService()
	{
		return aciOrderService;
	}

	/**
	 * @param aciOrderService
	 *           the aciOrderService to set
	 */
	public void setAciOrderService(final ACIOrderService aciOrderService)
	{
		this.aciOrderService = aciOrderService;
	}

}
