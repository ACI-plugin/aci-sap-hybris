/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient;

import java.util.Map;




public class HttpACIRequest
{

	/** HTTP GET method */
	public static final String METHOD_GET = "GET";

	/** HTTP POST method */
	public static final String METHOD_POST = "POST";

	public static final String METHOD_DELETE = "DELETE";


	private String url = null;

	private String method = METHOD_POST;

	private int timeout = 0;

	private int connectionTimeout = 0;

	private Map<String, String> paramsMap;

	private String charset = "GBK";


	private String clientIp;

	private HttpACIResultType resultType = HttpACIResultType.BYTES;

	private String token;



	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token
	 *           the token to set
	 */
	public void setToken(final String token)
	{
		this.token = token;
	}

	public HttpACIRequest(final HttpACIResultType resultType)
	{
		super();
		this.resultType = resultType;
	}

	/**
	 * @return Returns the clientIp.
	 */
	public String getClientIp()
	{
		return clientIp;
	}

	/**
	 * @param clientIp
	 *           The clientIp to set.
	 */
	public void setClientIp(final String clientIp)
	{
		this.clientIp = clientIp;
	}

	/**
	 * @return the paramsMap
	 */
	public Map<String, String> getParamsMap()
	{
		return paramsMap;
	}

	/**
	 * @param paramsMap
	 *           the paramsMap to set
	 */
	public void setParamsMap(final Map<String, String> paramsMap)
	{
		this.paramsMap = paramsMap;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(final String method)
	{
		this.method = method;
	}

	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	public void setConnectionTimeout(final int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout()
	{
		return timeout;
	}

	public void setTimeout(final int timeout)
	{
		this.timeout = timeout;
	}

	/**
	 * @return Returns the charset.
	 */
	public String getCharset()
	{
		return charset;
	}

	/**
	 * @param charset
	 *           The charset to set.
	 */
	public void setCharset(final String charset)
	{
		this.charset = charset;
	}

	public HttpACIResultType getResultType()
	{
		return resultType;
	}

	public void setResultType(final HttpACIResultType resultType)
	{
		this.resultType = resultType;
	}

}
