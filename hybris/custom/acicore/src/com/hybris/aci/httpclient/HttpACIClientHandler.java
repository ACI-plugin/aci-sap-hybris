/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient;


import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class HttpACIClientHandler
{
	private static final Logger LOG = Logger.getLogger(HttpACIClientHandler.class);

	private final HttpConnectionManager connectionManager;

	private static HttpACIClientHandler webHandler = new HttpACIClientHandler();

	private final int defaultConnectionTimeout = 8000;

	private static String DEFAULT_CHARSET = "GBK";

	private final int defaultSoTimeout = 30000;

	private final int defaultIdleConnTimeout = 60000;

	private final int defaultMaxConnPerHost = 30;

	private final int defaultMaxTotalConn = 80;

	private static final long DEFAULTHTTPCONNECTIONMANAGERTIMEOUT = 3 * 1000;

	private HttpACIProxy proxy;

	/**
	 * @return the proxy
	 */
	public HttpACIProxy getProxy()
	{
		return proxy;
	}


	/**
	 * @param proxy
	 *           the proxy to set
	 */
	public void setProxy(final HttpACIProxy proxy)
	{
		this.proxy = proxy;
	}


	public static HttpACIClientHandler getInstance()
	{
		return webHandler;
	}


	private HttpACIClientHandler()
	{
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
		connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

		final IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
		ict.addConnectionManager(connectionManager);
		ict.setConnectionTimeout(defaultIdleConnTimeout);

		ict.start();
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	public HttpACIResponse execute(final HttpACIRequest request)
	{
		final HttpClient httpclient = new HttpClient(connectionManager);
		if (this.proxy != null)
		{
			final HostConfiguration config = httpclient.getHostConfiguration();
			config.setProxy(proxy.getHost(), proxy.getPort());
			if (StringUtils.isNotEmpty(proxy.getUsername()) && StringUtils.isNotEmpty(proxy.getPassword()))
			{
				final Credentials credentials = new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword());
				final AuthScope authScope = new AuthScope(proxy.getHost(), proxy.getPort());
				httpclient.getState().setProxyCredentials(authScope, credentials);
			}
		}

		int connectionTimeout = defaultConnectionTimeout;
		if (request.getConnectionTimeout() > 0)
		{
			connectionTimeout = request.getConnectionTimeout();
		}
		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

		int soTimeout = defaultSoTimeout;
		if (request.getTimeout() > 0)
		{
			soTimeout = request.getTimeout();
		}
		httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

		httpclient.getParams().setConnectionManagerTimeout(DEFAULTHTTPCONNECTIONMANAGERTIMEOUT);

		String charset = request.getCharset();
		charset = charset == null ? DEFAULT_CHARSET : charset;

		final HttpMethod method = setHttpMethod(request, charset);

		final String auth = request.getToken();
		method.addRequestHeader("Authorization", "Bearer " + auth);
		method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);

		final HttpACIResponse response = new HttpACIResponse();

		try
		{
			httpclient.executeMethod(method);
			if (request.getResultType().equals(HttpACIResultType.STRING))
			{
				response.setStringResult(method.getResponseBodyAsString());
			}
			if (request.getResultType().equals(HttpACIResultType.BYTES))
			{
				response.setByteResult(method.getResponseBody());
			}
			response.setResponseHeaders(method.getResponseHeaders());
			response.setOk(true);
		}
		catch (final Exception ex)
		{
			response.setOk(false);
			response.setErrorMessage(ex.getMessage());
			LOG.error("HttpACIClientHandler - Exception occured" + ex.getStackTrace());
		}
		finally
		{
			method.releaseConnection();
		}
		return response;
	}

	private HttpMethod setHttpMethod(final HttpACIRequest request, final String charset)
	{
		HttpMethod method = null;
		if (request.getMethod().equals(HttpACIRequest.METHOD_GET))
		{
			method = new GetMethod(request.getUrl());
			method.getParams().setCredentialCharset(charset);
			method.setQueryString(toString(request.getParamsMap()));
			return method;
		}
		if (request.getMethod().equals(HttpACIRequest.METHOD_POST))
		{
			method = new PostMethod(request.getUrl());
			((PostMethod) method).addParameters(generatNameValuePair(request.getParamsMap()));
			return method;
			//method.addRequestHeader("User-Agent", "Mozilla/4.0");
		}
		if (request.getMethod().equals(HttpACIRequest.METHOD_DELETE))
		{
			method = new DeleteMethod(request.getUrl());
			((DeleteMethod) method).setQueryString(toString(request.getParamsMap()));
			return method;

		}
		return method;
	}

	private static NameValuePair[] generatNameValuePair(final Map<String, String> paramsMap)
	{
		final NameValuePair[] nameValuePair = new NameValuePair[paramsMap.size()];
		int i = 0;
		for (final Map.Entry<String, String> entry : paramsMap.entrySet())
		{
			nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
		}

		return nameValuePair;
	}

	protected String toString(final Map<String, String> paramsMap)

	{
		if (paramsMap == null || paramsMap.isEmpty())
		{
			return "null";
		}

		final StringBuffer buffer = new StringBuffer();

		int count = 0;
		for (final Map.Entry<String, String> entry : paramsMap.entrySet())
		{

			if (count == 0)
			{
				buffer.append(entry.getKey() + "=" + entry.getValue());
			}
			else
			{
				buffer.append("&" + entry.getKey() + "=" + entry.getValue());
			}
			count++;
		}

		return buffer.toString();
	}

}
