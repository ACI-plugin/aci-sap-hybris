/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Header;


public class HttpACIResponse
{

	private Header[] responseHeaders;

	private String stringResult;

	private byte[] byteResult;

	private boolean ok;

	private String errorMessage;

	public Header[] getResponseHeaders()
	{
		return responseHeaders;
	}

	public void setResponseHeaders(final Header[] responseHeaders)
	{
		this.responseHeaders = responseHeaders;
	}

	public byte[] getByteResult()
	{
		if (byteResult != null)
		{
			return byteResult;
		}
		if (stringResult != null)
		{
			return stringResult.getBytes();
		}
		return null;
	}

	public void setByteResult(final byte[] byteResult)
	{
		this.byteResult = byteResult;
	}

	public String getStringResult() throws UnsupportedEncodingException
	{
		if (stringResult != null)
		{
			return stringResult;
		}
		if (byteResult != null)
		{
			//	return new String(byteResult, PaymentConstants.Basic.INPUT_CHARSET);
		}
		return null;
	}

	public void setStringResult(final String stringResult)
	{
		this.stringResult = stringResult;
	}

	/**
	 * @return the ok
	 */
	public boolean isOk()
	{
		return ok;
	}

	/**
	 * @param ok
	 *           the ok to set
	 */
	public void setOk(final boolean ok)
	{
		this.ok = ok;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *           the errorMessage to set
	 */
	public void setErrorMessage(final String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

}
