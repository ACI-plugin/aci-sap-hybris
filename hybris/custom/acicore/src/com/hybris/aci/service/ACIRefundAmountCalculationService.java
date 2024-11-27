/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service;

import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.math.BigDecimal;


public interface ACIRefundAmountCalculationService
{
	BigDecimal getCustomRefundAmount(ReturnRequestModel var1);

	BigDecimal getCustomRefundEntryAmount(ReturnEntryModel var1);

	BigDecimal getOriginalRefundAmount(ReturnRequestModel var1);

	BigDecimal getOriginalRefundEntryAmount(ReturnEntryModel var1);
}