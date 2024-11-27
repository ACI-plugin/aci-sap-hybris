/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient.models;

public class CustomParameters
{
	String SHOPPER_savedCard;
	String testMode;

	/**
	 * @return the testMode
	 */
	public String getTestMode()
	{
		return testMode;
	}

	/**
	 * @param testMode
	 *           the testMode to set
	 */
	public void setTestMode(final String testMode)
	{
		this.testMode = testMode;
	}

	/**
	 * @return the forceResultCode
	 */
	public String getForceResultCode()
	{
		return forceResultCode;
	}

	/**
	 * @param forceResultCode
	 *           the forceResultCode to set
	 */
	public void setForceResultCode(final String forceResultCode)
	{
		this.forceResultCode = forceResultCode;
	}

	String forceResultCode;

	/**
	 * @return the sHOPPER_savedCard
	 */
	public String getSHOPPER_savedCard()
	{
		return SHOPPER_savedCard;
	}

	/**
	 * @param sHOPPER_savedCard
	 *           the sHOPPER_savedCard to set
	 */
	public void setSHOPPER_savedCard(final String sHOPPER_savedCard)
	{
		SHOPPER_savedCard = sHOPPER_savedCard;
	}

}
