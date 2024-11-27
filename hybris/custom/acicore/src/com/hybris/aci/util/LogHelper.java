/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.util;

import org.apache.log4j.Logger;


public class LogHelper
{
	public static void debugLog(final Logger LOG, final String message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message);
		}
	}
}
