/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient.models;

public class BaseResponse
{
	private boolean ok;
	private String errorCode;
	private String errorMessage;
	private Result result;
	private String buildNumber;
	private String timestamp;
	private String id;

	/**
	 * @return the ok
	 */
	public boolean isOk()
	{
		return ok;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * @return the result
	 */
	public Result getResult()
	{
		return result;
	}

	/**
	 * @return the buildNumber
	 */
	public String getBuildNumber()
	{
		return buildNumber;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
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
	 * @param errorMessage
	 *           the errorMessage to set
	 */
	public void setErrorMessage(final String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * @param result
	 *           the result to set
	 */
	public void setResult(final Result result)
	{
		this.result = result;
	}

	/**
	 * @param buildNumber
	 *           the buildNumber to set
	 */
	public void setBuildNumber(final String buildNumber)
	{
		this.buildNumber = buildNumber;
	}

	/**
	 * @param timestamp
	 *           the timestamp to set
	 */
	public void setTimestamp(final String timestamp)
	{
		this.timestamp = timestamp;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

	/**
	 * @param errorCode
	 *           the errorCode to set
	 */
	public void setErrorCode(final String errorCode)
	{
		this.errorCode = errorCode;
	}
}
