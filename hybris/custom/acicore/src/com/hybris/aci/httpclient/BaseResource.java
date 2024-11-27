/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient;

import java.util.Map;


public class BaseResource
{
	private String baseUrl;
	private String version;
	private String url;
	private HttpACIRequest aciRequest;


	/**
	 * make connection with ACI
	 *
	 * @param m
	 * @param method
	 * @param token
	 * @return
	 */
	public HttpACIResponse call(final Map m, final String method, final String token)
	{
		final HttpACIClientHandler webHandler = HttpACIClientHandler.getInstance();
		aciRequest = new HttpACIRequest(HttpACIResultType.STRING);
		aciRequest.setUrl(url);
		aciRequest.setMethod(method);
		if (m != null)
		{
			aciRequest.setParamsMap(m);
		}
		aciRequest.setToken(token);
		final HttpACIResponse response = webHandler.execute(aciRequest);

		return response;
	}


	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * @param baseUrl
	 *           the baseUrl to set
	 */
	public void setBaseUrl(final String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url
	 *           the url to set
	 */
	public void setUrl(final String url)
	{
		this.url = url;
	}

	/**
	 * @return the aciRequest
	 */
	public HttpACIRequest getAciRequest()
	{
		return aciRequest;
	}

	/**
	 * @param aciRequest
	 *           the aciRequest to set
	 */
	public void setAciRequest(final HttpACIRequest aciRequest)
	{
		this.aciRequest = aciRequest;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @param version
	 *           the version to set
	 */
	public void setVersion(final String version)
	{
		this.version = version;
	}
}
