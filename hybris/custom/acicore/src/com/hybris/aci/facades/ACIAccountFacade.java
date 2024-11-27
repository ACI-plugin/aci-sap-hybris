/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades;

import java.util.List;

import com.aci.payment.data.ACIPaymentInfoData;


/**
 * ACI Account Facade Interface
 */
public interface ACIAccountFacade
{
	/**
	 *
	 * @return List<ACIPaymentInfoData>
	 */
	List<ACIPaymentInfoData> getACIPaymentInfos();

	/**
	 * @param id
	 * @return String
	 */
	String unlinkACIPaymentInfo(final String id);

	/**
	 * @param resourcePath
	 *
	 */
	void createAciPaymentInfo(final String resourcePath);
}
