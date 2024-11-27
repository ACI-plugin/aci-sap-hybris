/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient;

/**
 *
 */
public class HttpACIProxy
{
	String host;

	int port;

	String username;

	String password;

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @param port
	 *           the port to set
	 */
	public void setPort(final int port)
	{
		this.port = port;
	}

	/**
	 * @return the host
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * @param host
	 *           the host to set
	 */
	public void setHost(final String host)
	{
		this.host = host;
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username
	 *           the username to set
	 */
	public void setUsername(final String username)
	{
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}


}
