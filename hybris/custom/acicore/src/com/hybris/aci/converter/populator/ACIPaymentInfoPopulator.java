/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.aci.payment.data.ACIPaymentInfoData;


/**
 *
 */
public class ACIPaymentInfoPopulator implements Populator<PaymentInfoModel, ACIPaymentInfoData>
{
	@Override
	public void populate(final PaymentInfoModel source, final ACIPaymentInfoData target) throws ConversionException
	{
		target.setId(source.getPk().toString());
		target.setAciCardNumber(source.getAciCardNumber());
		target.setAciCardExpiryMonth(source.getAciCardExpiryMonth());
		target.setAciCardExpiryYear(source.getAciCardExpiryYear());
		target.setAciCardHolder(source.getAciCardHolder());
		target.setAciPaymentType(source.getAciPaymentType());
		target.setAciPaymentBrand(source.getAciPaymentBrand());
		target.setAciRegistrationId(source.getAciRegistrationId());
	}
}