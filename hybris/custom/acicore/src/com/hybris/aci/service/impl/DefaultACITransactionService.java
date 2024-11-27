/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.aci.model.ACIResponseModel;
import com.hybris.aci.actions.order.ACITakePaymentAction;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.service.ACITransactionService;
import com.hybris.aci.util.ACIUtil;
import com.hybris.aci.util.LogHelper;


public class DefaultACITransactionService implements ACITransactionService
{
	private static final Logger LOG = Logger.getLogger(ACITakePaymentAction.class);

	public static final String PAYMENT_PROVIDER = "ACI";


	private ModelService modelService;
	private CommonI18NService commonI18NService;

	/**
	 * Saves transactions
	 *
	 * @param aciAuthorizationResponse
	 * @param abstractOrderModel
	 */
	@Override
	public void saveTransactionResponse(final ACIPaymentProcessResponse aciAuthorizationResponse,
			final AbstractOrderModel abstractOrderModel)
	{
		LogHelper.debugLog(LOG, "DefaultACITransactionService saveTransactionResponse - Start");

		final List<ACIResponseModel> aciResponses = abstractOrderModel.getAciResponse();
		final int transactionNo = aciResponses.size() + 1;

		final ACIResponseModel aciResponseModel = modelService.create(ACIResponseModel.class);
		aciResponseModel.setPaymentType(aciAuthorizationResponse.getPaymentType());
		aciResponseModel.setResult(aciAuthorizationResponse.getResult().getCode());
		aciResponseModel.setResultDetails(aciAuthorizationResponse.getResult().getDescription() + ACIUtilConstants.AMOUNT
				+ aciAuthorizationResponse.getAmount() + ACIUtilConstants.CURRENCY + aciAuthorizationResponse.getCurrency());
		aciResponseModel.setTransactionId(aciAuthorizationResponse.getId());
		aciResponseModel.setTransactionTimeStamp(aciAuthorizationResponse.getTimestamp());
		aciResponseModel.setTransactionNumber(new Integer(transactionNo));
		aciResponseModel.setOrder(abstractOrderModel);

		abstractOrderModel.setAciResponse(aciResponses);

		modelService.saveAll();
	}

	/**
	 * Creates payment Transaction
	 *
	 * @param aciAuthorizationResponse
	 * @param abstractOrderModel
	 * @return
	 */
	@Override
	public PaymentTransactionModel createPaymentTransaction(final ACIPaymentProcessResponse aciAuthorizationResponse,
			final AbstractOrderModel abstractOrderModel)
	{
		LogHelper.debugLog(LOG, "DefaultACITransactionService createPaymentTransaction - Start");

		final String transactionId = aciAuthorizationResponse.getId();
		final PaymentTransactionModel paymentTransactionModel = modelService.create(PaymentTransactionModel.class);
		paymentTransactionModel.setCode(transactionId);
		paymentTransactionModel.setRequestId(transactionId);
		paymentTransactionModel.setRequestToken(abstractOrderModel.getCode());
		paymentTransactionModel.setPaymentProvider(PAYMENT_PROVIDER);
		paymentTransactionModel.setOrder(abstractOrderModel);
		paymentTransactionModel.setCurrency(abstractOrderModel.getCurrency());
		paymentTransactionModel.setInfo(abstractOrderModel.getPaymentInfo());
		paymentTransactionModel.setPlannedAmount(new BigDecimal(aciAuthorizationResponse.getAmount()));

		return paymentTransactionModel;
	}

	/**
	 * Creates paymenttranscation entries
	 *
	 * @param paymentTransaction
	 * @param aciPaymentProcessResponse
	 * @return
	 */
	@Override
	public PaymentTransactionEntryModel createPaymentTransactionEntryModel(final PaymentTransactionModel paymentTransaction,
			final ACIPaymentProcessResponse aciPaymentProcessResponse)
	{
		LogHelper.debugLog(LOG, "DefaultACITransactionService createPaymentTransactionEntryModel - Start");


		final String aciPaymentStatus = ACIUtil.processResultCode(aciPaymentProcessResponse.getResult().getCode());

		final PaymentTransactionEntryModel transactionEntryModel = modelService.create(PaymentTransactionEntryModel.class);


		transactionEntryModel.setTransactionStatus(retrieveTransactionSatatus(aciPaymentStatus));

		transactionEntryModel.setPaymentTransaction(paymentTransaction);
		transactionEntryModel.setRequestId(aciPaymentProcessResponse.getId());
		if (aciPaymentProcessResponse.getReferenceId() != null)
		{
			transactionEntryModel.setRequestToken(aciPaymentProcessResponse.getReferenceId());
		}
		else
		{
			transactionEntryModel.setRequestToken(aciPaymentProcessResponse.getId());

		}
		final PaymentTransactionType transactionType = retrieveTransactionType(aciPaymentProcessResponse.getPaymentType());

		LOG.info("createPaymentTransactionEntryModel  aci payment status - " + aciPaymentStatus);

		LOG.info("createPaymentTransactionEntryModel  transaction type - " + transactionType);
		transactionEntryModel.setType(transactionType);

		transactionEntryModel.setCode(getNewPaymentTransactionEntryCode(paymentTransaction, transactionType));
		transactionEntryModel.setTime(DateTime.now().toDate());
		if (aciPaymentProcessResponse.getAmount() != null)
		{
			transactionEntryModel.setAmount(new BigDecimal(aciPaymentProcessResponse.getAmount()));
		}
		if (aciPaymentProcessResponse.getCurrency() != null)
		{
			transactionEntryModel.setCurrency(commonI18NService.getCurrency(aciPaymentProcessResponse.getCurrency()));
		}

		return transactionEntryModel;
	}

	private String retrieveTransactionSatatus(final String aciPaymentStatus)
	{
		if (ACIUtilConstants.PAYMENT_PENDING.equalsIgnoreCase(aciPaymentStatus))
		{
			return TransactionStatus.PENDING.name();
		}
		if (ACIUtilConstants.PAYMENT_REVIEW.equalsIgnoreCase(aciPaymentStatus))
		{
			return TransactionStatus.REVIEW.name();
		}
		if (ACIUtilConstants.PAYMENT_CONFIRMED.equalsIgnoreCase(aciPaymentStatus))
		{
			return TransactionStatus.ACCEPTED.name();
		}
		if (ACIUtilConstants.PAYMENT_REJECTED.equalsIgnoreCase(aciPaymentStatus))
		{
			return TransactionStatus.REJECTED.name();
		}
		return null;
	}

	private PaymentTransactionType retrieveTransactionType(final String paymentType)
	{

		if (paymentType.equals(ACIUtilConstants.CAPTURE_CODE))
		{
			return PaymentTransactionType.CAPTURE;
		}
		if (paymentType.equals(ACIUtilConstants.REVERSAL_CODE))
		{
			return PaymentTransactionType.CANCEL;
		}
		if (paymentType.equals(ACIUtilConstants.REFUND_CODE))
		{
			return PaymentTransactionType.REFUND_STANDALONE;
		}
		if (paymentType.equals(ACIUtilConstants.DEBIT_CODE))
		{
			return PaymentTransactionType.CAPTURE;
		}
		return PaymentTransactionType.AUTHORIZATION;
	}

	private String getNewPaymentTransactionEntryCode(final PaymentTransactionModel transaction,
			final PaymentTransactionType paymentTransactionType)
	{
		return transaction.getEntries() == null ? transaction.getCode() + "-" + paymentTransactionType.getCode() + "-1"
				: transaction.getCode() + "-" + paymentTransactionType.getCode() + "-" + (transaction.getEntries().size() + 1);
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

}
