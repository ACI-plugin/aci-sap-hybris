/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aci.model.ACIConfigModel;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.httpclient.ACIPayment;
import com.hybris.aci.httpclient.CheckoutOrder;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.populator.DataPopulator;
import com.hybris.aci.service.ACIConfigService;
import com.hybris.aci.service.ACIPaymentService;
import com.hybris.aci.service.ACITransactionService;
import com.hybris.aci.util.ACIUtil;
import com.hybris.aci.util.LogHelper;


public class DefaultACIPaymentService implements ACIPaymentService
{
	private ACIConfigService aciConfigService;
	private CommonI18NService commonI18NService;
	private ModelService modelService;
	private ACITransactionService aciTransactionService;
	private static final Logger LOG = Logger.getLogger(DefaultACIPaymentService.class);
	private static final String LOG_MSG = "Return transactional entry created: ";

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
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the aciConfigService
	 */
	public ACIConfigService getAciConfigService()
	{
		return aciConfigService;
	}

	/**
	 * @param aciConfigService
	 *           the aciConfigService to set
	 */
	public void setAciConfigService(final ACIConfigService aciConfigService)
	{
		this.aciConfigService = aciConfigService;
	}

	/**
	 * Does ACI Capture
	 *
	 * @param order
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse capture(final AbstractOrderModel order)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService Capture - Start");
		ACIPaymentProcessResponse response = null;

		for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
		{

			PaymentTransactionEntryModel auth = null;
			final Iterator ite = transaction.getEntries().iterator();

			while (ite.hasNext())
			{
				final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) ite.next();
				if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
				{
					auth = pte;
					break;
				}
			}

			if (auth == null)
			{
				LOG.error("DefaultACIPaymentService Capture - Could not capture without authorization");

				throw new AdapterException("Could not capture without authorization");
			}
			else
			{

				response = processPaymentActions(order, String.format("%.2f", auth.getAmount()), auth.getCurrency().getIsocode(),
						auth.getRequestId(), ACIUtilConstants.CAPTURE);

				final PaymentTransactionEntryModel captureEntry = getAciTransactionService()
						.createPaymentTransactionEntryModel(transaction, response);
				LogHelper.debugLog(LOG, LOG_MSG + captureEntry.getRequestId());

				getModelService().saveAll();

			}


		}
		LogHelper.debugLog(LOG, "DefaultACIPaymentService Capture - END");

		return response;
	}

	/**
	 * Does ACI partial Capture
	 *
	 * @param order
	 * @param amount
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse partialCapture(final AbstractOrderModel order, final String amount)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService partialCapture - Start");

		ACIPaymentProcessResponse response = null;

		for (final PaymentTransactionModel transaction : order.getPaymentTransactions())
		{

			PaymentTransactionEntryModel auth = null;
			final Iterator ite = transaction.getEntries().iterator();

			while (ite.hasNext())
			{
				final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) ite.next();
				if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
				{
					auth = pte;
					break;
				}
			}

			if (auth == null)
			{
				throw new AdapterException("Could not capture without authorization");
			}
			else
			{
				//TODO - may handle sending the order items for partial capture
				response = processPaymentActions(order, amount, auth.getCurrency().getIsocode(), auth.getRequestId(),
						ACIUtilConstants.CAPTURE);

				if (response != null)
				{
					final PaymentTransactionEntryModel captureEntry = getAciTransactionService()
							.createPaymentTransactionEntryModel(transaction, response);
					LogHelper.debugLog(LOG, LOG_MSG + captureEntry.getRequestId());
					getModelService().saveAll();
				}
			}


		}
		LogHelper.debugLog(LOG, "DefaultACIPaymentService partialCapture - END");
		return response;
	}

	/**
	 * Does refund
	 *
	 * @param order
	 * @param transaction
	 * @param amount
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse refund(final AbstractOrderModel order, final PaymentTransactionModel transaction,
			final String amount)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService refund - Start");
		ACIPaymentProcessResponse response = null;
		PaymentTransactionEntryModel captureTransaction = null;
		final Iterator ite = transaction.getEntries().iterator();

		while (ite.hasNext())
		{
			final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) ite.next();
			if (pte.getType().equals(PaymentTransactionType.CAPTURE))
			{
				captureTransaction = pte;
				break;
			}
		}

		if (captureTransaction == null)
		{
			throw new AdapterException("Could not refund without capture");
		}
		else
		{
			response = processPaymentActions(order, amount, captureTransaction.getCurrency().getIsocode(),
					captureTransaction.getRequestId(), ACIUtilConstants.REFUND);

			final PaymentTransactionEntryModel refundEntry = getAciTransactionService()
					.createPaymentTransactionEntryModel(captureTransaction.getPaymentTransaction(), response);
			LogHelper.debugLog(LOG, LOG_MSG + refundEntry.getRequestId());
			getModelService().saveAll();
		}
		LogHelper.debugLog(LOG, "DefaultACIPaymentService refund - End");
		return response;
	}

	/**
	 *
	 * @param order
	 * @param transactionEntry
	 * @param amount
	 * @param currency
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse refund(final AbstractOrderModel order, final PaymentTransactionEntryModel transactionEntry,
			final String amount, final String currency)
	{
		//TODO - may handle sending the order items for the refunded item details
		final ACIPaymentProcessResponse response = processPaymentActions(order, amount, currency, transactionEntry.getRequestId(),
				ACIUtilConstants.REFUND);

		final PaymentTransactionEntryModel refundEntry = getAciTransactionService()
				.createPaymentTransactionEntryModel(transactionEntry.getPaymentTransaction(), response);
		LogHelper.debugLog(LOG, LOG_MSG + refundEntry.getRequestId());
		getModelService().saveAll();
		return response;
	}

	/**
	 * Does reversal
	 *
	 * @param order
	 * @param transactionEntry
	 * @param amount
	 * @param currency
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse reversal(final AbstractOrderModel order, final PaymentTransactionEntryModel transactionEntry,
			final String amount, final String currency)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService reversal - Start");
		final ACIPaymentProcessResponse response = processPaymentActions(order, amount, currency, transactionEntry.getRequestId(),
				ACIUtilConstants.REVERSAL);

		final PaymentTransactionEntryModel returnEntry = getAciTransactionService()
				.createPaymentTransactionEntryModel(transactionEntry.getPaymentTransaction(), response);
		LogHelper.debugLog(LOG, LOG_MSG + returnEntry.getRequestId());
		getModelService().saveAll();
		LogHelper.debugLog(LOG, "DefaultACIPaymentService reversal - End");
		return response;
	}

	/**
	 * Does the ACI calls for backoffice operations
	 *
	 * @param order
	 * @param amount
	 * @param currency
	 * @param requestId
	 * @param action
	 * @return
	 */
	public ACIPaymentProcessResponse processPaymentActions(final AbstractOrderModel order, final String amount,
			final String currency, final String requestId, final String action)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService processPaymentActions - Start");
		final ACIConfigModel aciConfig = order.getStore().getACIConfig();

		final Map<String, String> m = new HashMap<String, String>();
		final DataPopulator dp = new DataPopulator();
		dp.populateAuthData(m, aciConfig.getEntityId());
		//TODO Set currency here
		dp.populateBaseDataForBackEnd(amount, currency, m, action);
		if (action.equals(ACIUtilConstants.CAPTURE))
		{
			// HERE Assumtion is that, Capture and reversal are not Partial
			dp.populateCartData(order, m);
		}

		final ACIPayment aciPayment = new ACIPayment(getAciConfigService().getBaseUrlFromConfig(),
				getAciConfigService().getAPiVersionFromConfig());
		final ACIPaymentProcessResponse aciPaymentProcessResponse = aciPayment.doPaymentAction(m, aciConfig.getBearerToken(),
				requestId);
		getAciTransactionService().saveTransactionResponse(aciPaymentProcessResponse, order);
		ACIUtil.setAciTransactionStatus(aciPaymentProcessResponse, order);
		LOG.info("DefaultACIPaymentService Capture - Response details - OK? " + aciPaymentProcessResponse.isOk() + " resultcode : "
				+ aciPaymentProcessResponse.getResult().getCode() + " error code : " + aciPaymentProcessResponse.getErrorCode()
				+ " ErrorMessage : " + aciPaymentProcessResponse.getErrorMessage());
		LogHelper.debugLog(LOG, "DefaultACIPaymentService processPaymentActions - End");
		return aciPaymentProcessResponse;
	}

	/**
	 * This method is used to get the payment status during orderplacement.
	 *
	 * @param resourcePath
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse getAciStatus(final String resourcePath)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService getAciStatus - Start");

		final ACIConfigModel aciConfig = getAciConfigService().getACIConfig();

		final Map<String, String> m = new HashMap<String, String>();
		final DataPopulator dp = new DataPopulator();
		dp.populateAuthData(m, aciConfig.getEntityId());
		final CheckoutOrder aciCheckoutOrder = new CheckoutOrder(getAciConfigService().getBaseUrl());
		final ACIPaymentProcessResponse aciStatusResponse = aciCheckoutOrder.getStatus(m, aciConfig.getBearerToken(), resourcePath);
		LogHelper.debugLog(LOG, "DefaultACIPaymentService getAciStatus - End");
		return aciStatusResponse;
	}

	/**
	 * Cancellation of order
	 *
	 * @param order
	 * @param transactionEntry
	 * @param amount
	 * @param currency
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse cancel(final OrderModel order, final PaymentTransactionEntryModel transactionEntry,
			final String amount, final String currency)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService cancel - Start");
		ACIPaymentProcessResponse aciPaymentProcessResponse = null;
		if (transactionEntry.getType().equals(PaymentTransactionType.AUTHORIZATION))
		{
			aciPaymentProcessResponse = reversal(order, transactionEntry, amount, currency);
		}
		if (transactionEntry.getType().equals(PaymentTransactionType.CAPTURE))
		{
			aciPaymentProcessResponse = refund(order, transactionEntry, amount, currency);

		}
		LogHelper.debugLog(LOG, "DefaultACIPaymentService cancel - End");

		return aciPaymentProcessResponse;
	}

	/**
	 * This method is used in the JOB to get the status of pending order and get the current status in API
	 *
	 * @param order
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse getPaymentStatus(final OrderModel order)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentService getPaymentStatus - Start");

		final ACIConfigModel aciConfig = order.getStore().getACIConfig();

		final Map<String, String> m = new HashMap<String, String>();
		final DataPopulator dp = new DataPopulator();
		dp.populateAuthData(m, aciConfig.getEntityId());
		final CheckoutOrder aciCheckoutOrder = new CheckoutOrder(aciConfigService.getBaseUrlFromConfig(),
				aciConfigService.getAPiVersionFromConfig());
		final ACIPaymentProcessResponse aciStatusResponse = aciCheckoutOrder.getPendingPaymentStatus(m, aciConfig.getBearerToken(),
				order.getAciPaymentId());
		aciTransactionService.saveTransactionResponse(aciStatusResponse, order);
		ACIUtil.setAciTransactionStatus(aciStatusResponse, order);
		LOG.info("DefaultACIPaymentService Capture - Response details - OK? " + aciStatusResponse.isOk() + " resultcode : "
				+ aciStatusResponse.getResult().getCode() + " error code : " + aciStatusResponse.getErrorCode() + " ErrorMessage : "
				+ aciStatusResponse.getErrorMessage());
		LogHelper.debugLog(LOG, "DefaultACIPaymentService getPaymentStatus - End");
		return aciStatusResponse;
	}
}
