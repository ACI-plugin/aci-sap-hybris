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


public class ACIPayment extends BaseResource
{
	private static final Logger LOG = Logger.getLogger(ACIPayment.class);

	public static final String URL = "/payments";
	public static final String SLASH = "/";


	public ACIPayment(final String baseUrl)
	{
		super();
		setBaseUrl(baseUrl);
	}

	public ACIPayment(final String baseUrl, final String version)
	{
		super();
		setBaseUrl(baseUrl);
		setVersion(version);
	}

	/**
	 * Does payment calls to ACI
	 *
	 * @param m
	 * @param token
	 * @param paymentId
	 * @return
	 */
	public ACIPaymentProcessResponse doPaymentAction(final Map<String, String> m, final String token, final String paymentId)
	{
		LogHelper.debugLog(LOG, "ACI Payment - doPaymentAction Start");
		final StringBuilder sb = new StringBuilder();
		if (paymentId != null)
		{
			setUrl(
					sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(URL).append(SLASH).append(paymentId).toString());
		}
		else
		{
			setUrl(sb.append(getBaseUrl()).append(URL).toString());
		}
		final HttpACIResponse paymentActionresponse = call(m, HttpACIRequest.METHOD_POST, token);
		ACIPaymentProcessResponse webCallResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = paymentActionresponse.getStringResult();
			LogHelper.debugLog(LOG, "ACI Payment - doPaymentAction response -  : " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				webCallResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);

			}
		}
		catch (final UnsupportedEncodingException e)
		{
			webCallResponse.setOk(false);
			webCallResponse.setErrorMessage("UnsupportedEncodingException Occured");
			LOG.error(e.getStackTrace());
		}
		webCallResponse.setOk(paymentActionresponse.isOk());
		webCallResponse.setErrorMessage(paymentActionresponse.getErrorMessage());

		return webCallResponse;
	}
}
