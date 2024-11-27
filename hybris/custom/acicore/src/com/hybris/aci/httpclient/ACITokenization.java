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


public class ACITokenization extends BaseResource
{
	private static final Logger LOG = Logger.getLogger(ACITokenization.class);

	public static final String URL = "/registrations";
	public static final String SLASH = "/";
	private static final String ERROR_MSG = "UnsupportedEncodingException Occured";

	public ACITokenization(final String baseUrl, final String version)
	{
		super();
		setBaseUrl(baseUrl);
		setVersion(version);
	}

	/**
	 * Tokeniza the card
	 *
	 * @param m
	 * @param token
	 * @return
	 */
	public ACIPaymentProcessResponse tokenizeCard(final Map<String, String> m, final String token)
	{
		LogHelper.debugLog(LOG, "ACITokenization tokenizeCard - Start");
		final StringBuilder sb = new StringBuilder();
		setUrl(sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(URL).toString());

		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_POST, token);
		ACIPaymentProcessResponse tokenizationResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "ACITokenization tokenizeCard-  responseResult: " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				tokenizationResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);

			}


		}
		catch (final UnsupportedEncodingException e)
		{
			tokenizationResponse.setOk(false);
			tokenizationResponse.setErrorMessage(ERROR_MSG);
			LOG.error(e.getStackTrace());
		}

		return tokenizationResponse;
	}

	/**
	 * Get the ACI Token
	 *
	 * @param m
	 * @param token
	 * @param registration
	 * @return
	 */
	public ACIPaymentProcessResponse getAcitoken(final Map<String, String> m, final String token, final String registration)
	{
		LogHelper.debugLog(LOG, "ACITokenization getAcitoken - Start");
		final StringBuffer sb = new StringBuffer();
		setUrl(
				sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(URL).append(SLASH).append(registration).toString());

		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_GET, token);
		ACIPaymentProcessResponse tokenizationResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "ACITokenization getAcitoken - responseResult: " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				tokenizationResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);

			}


		}
		catch (final UnsupportedEncodingException e)
		{
			tokenizationResponse.setOk(false);
			tokenizationResponse.setErrorMessage(ERROR_MSG);
			LOG.error(e.getStackTrace());
		}
		tokenizationResponse.setOk(response.isOk());
		tokenizationResponse.setErrorMessage(response.getErrorMessage());

		return tokenizationResponse;
	}

	/**
	 * Delete token
	 *
	 * @param m
	 * @param bearerToken
	 * @param registration
	 * @return
	 */
	public ACIPaymentProcessResponse deleteAcitoken(final Map<String, String> m, final String bearerToken,
			final String registration)
	{
		LogHelper.debugLog(LOG, "ACITokenization deleteAcitoken - Start");

		final StringBuffer sb = new StringBuffer();
		setUrl(
				sb.append(getBaseUrl()).append(SLASH).append(getVersion()).append(URL).append(SLASH).append(registration).toString());

		final HttpACIResponse response = call(m, HttpACIRequest.METHOD_DELETE, bearerToken);
		ACIPaymentProcessResponse tokenizationResponse = new ACIPaymentProcessResponse();
		try
		{
			final String responseResult = response.getStringResult();
			LogHelper.debugLog(LOG, "ACITokenization deleteAcitoken - responseResult: " + responseResult);
			if (StringUtils.isNotEmpty(responseResult))
			{
				final Gson gson = new Gson();
				tokenizationResponse = gson.fromJson(responseResult, ACIPaymentProcessResponse.class);
			}

		}
		catch (final UnsupportedEncodingException e)
		{
			tokenizationResponse.setOk(false);
			tokenizationResponse.setErrorMessage(ERROR_MSG);
			LOG.error(e.getStackTrace());
		}
		tokenizationResponse.setOk(response.isOk());
		tokenizationResponse.setErrorMessage(response.getErrorMessage());

		return tokenizationResponse;
	}

}
