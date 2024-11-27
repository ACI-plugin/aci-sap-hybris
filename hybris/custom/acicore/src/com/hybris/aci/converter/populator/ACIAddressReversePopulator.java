/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.converter.populator;

import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 */
public class ACIAddressReversePopulator implements Populator<AddressData, AddressModel>
{

	AddressReversePopulator addressReversePopulator;

	/**
	 * @return the addressReversePopulator
	 */
	public AddressReversePopulator getAddressReversePopulator()
	{
		return addressReversePopulator;
	}


	/**
	 * @param addressReversePopulator
	 *           the addressReversePopulator to set
	 */
	public void setAddressReversePopulator(final AddressReversePopulator addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}


	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		addressReversePopulator.populate(addressData, addressModel);
		addressModel.setStreetname(addressData.getLine1());
		addressModel.setStreetnumber(addressData.getLine2());


	}
}
