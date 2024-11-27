/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.util.LogHelper;


public class CheckoutOrder extends BaseResource
{
	private static final Logger LOG = Logger.getLogger(CheckoutOrder.class);

	public static final String CHECKOUT = "/checkouts";
	public static final String QUERY = "/query/";
	public static final String SLASH = "/";
	private static final String ERROR_MSG = "UnsupportedEncodingException Occured";

	public CheckoutOrder(final String baseUrl, final String version)
	{
		super();
		setBaseUrl(baseUrl);
		setVersion(version);
	}

	public CheckoutOrder(final String baseUrl)
	{
		super();
		setBaseUrl(baseUrl);
	}

	/**
	 * prepare checkout call
	 *
	 * @param m
	 * @param token
	 * @param checkoutId
	 * @return
	 */
	public ACIPaymentProcessResponse prepareCheckout(final Map<String, String> m, final String token, final String checkoutId)
	{
		LogHelper.debugLog(LOG, "CheckoutOrder prepareCheckout - Start");
		final StringBuffer sb = new StringBuffer();
		if (checkoutId != null)
		{
			setUrl(sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(CHECKOUT).append(SLASH).append(checkoutId)
					.toString());
		}
		else
		{
			setUrl(sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(CHECKOUT).toString());
		}
		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_POST, token);
		ACIPaymentProcessResponse prepareCheckoutResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "CheckoutOrder prepareCheckout - responseResult: " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				prepareCheckoutResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);
			}

		}
		catch (final UnsupportedEncodingException e)
		{
			prepareCheckoutResponse.setOk(false);
			prepareCheckoutResponse.setErrorMessage(ERROR_MSG);
			LOG.error(e.getStackTrace());
		}
		prepareCheckoutResponse.setOk(response.isOk());
		prepareCheckoutResponse.setErrorMessage(response.getErrorMessage());

		return prepareCheckoutResponse;
	}

	/**
	 * Get status of payment call
	 *
	 * @param m
	 * @param token
	 * @param resourcePath
	 * @return
	 */
	public ACIPaymentProcessResponse getStatus(final Map<String, String> m, final String token, final String resourcePath)
	{
		LogHelper.debugLog(LOG, "CheckoutOrder getStatus - Start");
		final StringBuffer sb = new StringBuffer();

		setUrl(sb.append(getBaseUrl()).append(SLASH).append(resourcePath).toString());
		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_GET, token);
		ACIPaymentProcessResponse aciStatusResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "CheckoutOrder getStatus - responseResult: " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				aciStatusResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);
			}

		}
		catch (final UnsupportedEncodingException e)
		{
			aciStatusResponse.setOk(false);
			aciStatusResponse.setErrorMessage(ERROR_MSG);
			LOG.error(e.getStackTrace());
		}
		aciStatusResponse.setOk(response.isOk());
		aciStatusResponse.setErrorMessage(response.getErrorMessage());
		return aciStatusResponse;
	}

	/**
	 * Get status of pending orders
	 *
	 * @param m
	 * @param token
	 * @param paymentId
	 * @return
	 */
	public ACIPaymentProcessResponse getPendingPaymentStatus(final Map<String, String> m, final String token,
			final String paymentId)
	{
		LogHelper.debugLog(LOG, "CheckoutOrder getPendingPaymentStatus - Start");
		final StringBuffer sb = new StringBuffer();

		setUrl(sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(QUERY).append(paymentId).toString());
		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_GET, token);
		ACIPaymentProcessResponse aciStatusResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "CheckoutOrder getPendingPaymentStatus - responseResult: " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				aciStatusResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);
			}

		}
		catch (final UnsupportedEncodingException e)
		{
			aciStatusResponse.setOk(false);
			aciStatusResponse.setErrorMessage(ERROR_MSG);
			LOG.error(e.getStackTrace());
		}

		aciStatusResponse.setOk(response.isOk());
		aciStatusResponse.setErrorMessage(response.getErrorMessage());

		return aciStatusResponse;
	}
}
