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


public class ACIRegistration extends BaseResource
{
	private static final Logger LOG = Logger.getLogger(ACIRegistration.class);

	public static final String URL = "/registrations ";
	public static final String SLASH = "/";


	public ACIRegistration(final String baseUrl)
	{
		super();
		setBaseUrl(baseUrl);
	}

	public ACIRegistration(final String baseUrl, final String version)
	{
		super();
		setBaseUrl(baseUrl);
		setVersion(version);
	}

	/**
	 * ACI Tokernizations
	 *
	 * @param m
	 * @param token
	 * @param paymentId
	 * @return
	 */
	public ACIPaymentProcessResponse createRegistration(final Map<String, String> m, final String token, final String paymentId)
	{
		LogHelper.debugLog(LOG, "ACIRegistration createRegistration - Start");
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
		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_POST, token);
		ACIPaymentProcessResponse webCallResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "ACIRegistration createRegistration - responseResult - : " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				webCallResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);
				webCallResponse.setOk(response.isOk());
				webCallResponse.setErrorMessage(response.getErrorMessage());

			}
		}
		catch (final UnsupportedEncodingException e)
		{
			webCallResponse.setOk(false);
			webCallResponse.setErrorMessage("UnsupportedEncodingException Occured");
			LOG.error(e.getStackTrace());
		}

		return webCallResponse;
	}
}
