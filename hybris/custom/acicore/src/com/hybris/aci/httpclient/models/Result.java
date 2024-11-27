/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient.models;

public class Result
{

	private String code;
	private String description;
	private String avsResponse;
	private String cvvResponse;

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *           the description to set
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * @return the avsResponse
	 */
	public String getAvsResponse()
	{
		return avsResponse;
	}

	/**
	 * @param avsResponse
	 *           the avsResponse to set
	 */
	public void setAvsResponse(final String avsResponse)
	{
		this.avsResponse = avsResponse;
	}

	/**
	 * @return the cvvResponse
	 */
	public String getCvvResponse()
	{
		return cvvResponse;
	}

	/**
	 * @param cvvResponse
	 *           the cvvResponse to set
	 */
	public void setCvvResponse(final String cvvResponse)
	{
		this.cvvResponse = cvvResponse;
	}



}
