/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient.models;

public class Customer
{

	private String givenName;
	private String surname;
	private String email;

	/**
	 * @return the givenName
	 */
	public String getGivenName()
	{
		return givenName;
	}

	/**
	 * @param givenName
	 *           the givenName to set
	 */
	public void setGivenName(final String givenName)
	{
		this.givenName = givenName;
	}

	/**
	 * @return the surname
	 */
	public String getSurname()
	{
		return surname;
	}

	/**
	 * @param surname
	 *           the surname to set
	 */
	public void setSurname(final String surname)
	{
		this.surname = surname;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}
}
