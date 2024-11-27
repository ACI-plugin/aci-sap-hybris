/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient.models;

public class Card
{
	private String last4Digits;
	private String bin;
	private String holder;
	private String expiryMonth;
	private String expiryYear;

	/**
	 * @return the bin
	 */
	public String getBin()
	{
		return bin;
	}

	/**
	 * @param bin
	 *           the bin to set
	 */
	public void setBin(final String bin)
	{
		this.bin = bin;
	}

	/**
	 * @return the holder
	 */
	public String getHolder()
	{
		return holder;
	}

	/**
	 * @param holder
	 *           the holder to set
	 */
	public void setHolder(final String holder)
	{
		this.holder = holder;
	}

	/**
	 * @return the expiryMonth
	 */
	public String getExpiryMonth()
	{
		return expiryMonth;
	}

	/**
	 * @param expiryMonth
	 *           the expiryMonth to set
	 */
	public void setExpiryMonth(final String expiryMonth)
	{
		this.expiryMonth = expiryMonth;
	}

	/**
	 * @return the expiryYear
	 */
	public String getExpiryYear()
	{
		return expiryYear;
	}

	/**
	 * @param expiryYear
	 *           the expiryYear to set
	 */
	public void setExpiryYear(final String expiryYear)
	{
		this.expiryYear = expiryYear;
	}

	/**
	 * @return the last4Digits
	 */
	public String getLast4Digits()
	{
		return last4Digits;
	}

	/**
	 * @param last4Digits
	 *           the last4Digits to set
	 */
	public void setLast4Digits(final String last4Digits)
	{
		this.last4Digits = last4Digits;
	}
}
