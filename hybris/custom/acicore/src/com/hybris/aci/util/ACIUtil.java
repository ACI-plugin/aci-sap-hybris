/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.util;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.aci.payment.data.ACIConfigData;
import com.aci.payment.data.ACIPaymentMethodsData;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;


public class ACIUtil
{
	private static final String REGEX_CONFIRMED = "[0][0][0].[0][0][0].\\d\\d\\d|[0][0][0].[1][0][0].[1]\\d\\d|[0][0][0].[36]\\d\\d.\\d\\d\\d";
	private static final String REGEX_REVIEW = "[0][0][0].[4][0][0].[0][^3]\\d|[0][0][0].[4][0][0].[1][0][0]";
	private static final String REGEX_PENDING = "[0][0][0].[2][0][0].*";
	private static final String REGEX_REJECTED = "[8][0][0].[4][0][0].5\\d\\d|[1][0][0].[4][0][0].[5][0][0]";
	private static final String SEPERATOR = " ";

	private ACIUtil()
	{
		throw new IllegalStateException("Utility class");
	}

	public static String processResultCode(final String resultCode)
	{

		//successfully processed transactions
		if (resultCode.matches(REGEX_CONFIRMED))
		{
			return ACIUtilConstants.PAYMENT_CONFIRMED;
		}
		//Result codes for successfully processed transactions that should be manually reviewed
		else if (resultCode.matches(REGEX_REVIEW))
		{
			return ACIUtilConstants.PAYMENT_REVIEW;
		}
		//Result codes for pending transactions
		else if (resultCode.matches(REGEX_PENDING))
		{
			return ACIUtilConstants.PAYMENT_PENDING;
		}
		//Result codes for pending transactions - temporariry rejected
		else if (resultCode.matches(REGEX_REJECTED))
		{
			return ACIUtilConstants.PAYMENT_PENDING;
		}
		else
		{
			return ACIUtilConstants.PAYMENT_REJECTED;
		}

	}

	public static void setAciTransactionStatus(final ACIPaymentProcessResponse aciAuthorizationResponse,
			final AbstractOrderModel order)
	{
		if (!aciAuthorizationResponse.isOk())
		{
			aciAuthorizationResponse.setErrorCode(ACIUtilConstants.ACI_SERVICE_ERROR);
			return;
		}

		if (order.getAciMerchantTransactionId() != null)
		{
			// - Order is present and is a ACI backoffice operation
			if (aciAuthorizationResponse.getMerchantTransactionId() != null
					&& !aciAuthorizationResponse.getMerchantTransactionId().equalsIgnoreCase(order.getAciMerchantTransactionId()))
			{
				aciAuthorizationResponse.setErrorCode(ACIUtilConstants.ORDER_MISMATCH);
				aciAuthorizationResponse.setOk(false);
				return;
			}
		}

		else
		{
			// - Order is not present and hence its still cart operations
			if (aciAuthorizationResponse.getMerchantTransactionId() != null
					&& !aciAuthorizationResponse.getMerchantTransactionId().equalsIgnoreCase("ACI" + order.getCode()))
			{
				aciAuthorizationResponse.setErrorCode(ACIUtilConstants.SESSION_INVALID);
				aciAuthorizationResponse.setOk(false);
				return;
			}
		}


		if (processResultCode(aciAuthorizationResponse.getResult().getCode()).equalsIgnoreCase(ACIUtilConstants.PAYMENT_REJECTED))
		{
			aciAuthorizationResponse.setErrorCode(ACIUtilConstants.TRANSACTION_REJECTED);
			if (aciAuthorizationResponse.getResult().getCode().equalsIgnoreCase(ACIUtilConstants.CODE_CUSTOMER_CANEL))
			{
				aciAuthorizationResponse.setErrorCode(ACIUtilConstants.CANCELLED_BY_CUSTOMER);

			}
			aciAuthorizationResponse.setOk(false);
			return;
		}


	}

	public static String getBrands(final ACIConfigData aciConfig)
	{
		final StringBuilder payBrands = new StringBuilder(StringUtils.EMPTY);
		if (aciConfig.getAllowedPaymentMethods() != null)
		{
			for (final ACIPaymentMethodsData aciPaymentMethod : aciConfig.getAllowedPaymentMethods())
			{
				if (payBrands.isEmpty())
				{
					payBrands.append(aciPaymentMethod.getCode());
				}
				else
				{
					payBrands.append(SEPERATOR).append(aciPaymentMethod.getCode());
				}
			}

		}
		return payBrands.toString();
	}

	public static String getRegistrationAllowedCards(final ACIConfigData aciConfig)
	{
		String cardBrands = "";
		if (aciConfig.getAllowedPaymentMethods() != null)
		{
			cardBrands = getCardBrands(aciConfig.getAllowedPaymentMethods());

		}

		return cardBrands;
	}

	private static String getCardBrands(final List<ACIPaymentMethodsData> paymentMethods)
	{
		String cardBrands = "";
		for (final ACIPaymentMethodsData aciPaymentMethod : paymentMethods)
		{
			if (aciPaymentMethod.getAllowRegstration())
			{
				if (cardBrands.isEmpty())
				{
					cardBrands = aciPaymentMethod.getCode();
				}
				else
				{
					cardBrands = cardBrands + SEPERATOR + aciPaymentMethod.getCode();
				}
			}
		}
		return cardBrands;
	}
}
