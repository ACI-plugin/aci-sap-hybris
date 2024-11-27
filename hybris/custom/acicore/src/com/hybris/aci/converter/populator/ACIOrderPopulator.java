/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.converter.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

import com.aci.payment.data.ACIPaymentInfoData;


public class ACIOrderPopulator extends AbstractOrderPopulator<AbstractOrderModel, AbstractOrderData>
{

	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<PaymentInfoModel, ACIPaymentInfoData> aciPaymentInfoConverter;

	/**
	 * @return the aciPaymentInfoConverter
	 */
	public Converter<PaymentInfoModel, ACIPaymentInfoData> getAciPaymentInfoConverter()
	{
		return aciPaymentInfoConverter;
	}

	/**
	 * @param aciPaymentInfoConverter
	 *           the aciPaymentInfoConverter to set
	 */
	public void setAciPaymentInfoConverter(final Converter<PaymentInfoModel, ACIPaymentInfoData> aciPaymentInfoConverter)
	{
		this.aciPaymentInfoConverter = aciPaymentInfoConverter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		target.setAciMerchantTransactionId(source.getAciMerchantTransactionId());
		target.setAciCheckoutId(source.getAciCheckoutId());
		target.setAciPaymentId(source.getAciPaymentId());
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo != null && isACIPaymentInfo(paymentInfo))
		{
			// In the OrderConfirmationPage, orderData.PaymentInfo is "required" to be a CCPaymentInfoData, and cannot be null.
			final CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
			ccPaymentInfoData.setBillingAddress(addressConverter.convert(paymentInfo.getBillingAddress()));
			target.setAciPaymentType(source.getPaymentInfo().getAciPaymentType());

			target.setPaymentInfo(ccPaymentInfoData);
		}
		target.setAciPaymentInfo(aciPaymentInfoConverter.convert(paymentInfo));
	}

	protected boolean isACIPaymentInfo(final PaymentInfoModel paymentInfo)
	{
		return !(paymentInfo instanceof CreditCardPaymentInfoModel);
	}

	@Override
	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}
}
