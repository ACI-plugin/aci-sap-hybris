/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import com.aci.model.ACIConfigModel;
import com.aci.model.ACIPaymentMethodsModel;
import com.aci.payment.data.ACIConfigData;
import com.aci.payment.data.ACIPaymentMethodsData;
import com.hybris.aci.constants.ACIUtilConstants;


public class ACIConfigPopulator implements Populator<ACIConfigModel, ACIConfigData>
{
	@Override
	public void populate(final ACIConfigModel source, final ACIConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		target.setBearerToken(source.getBearerToken());
		target.setEntityId(source.getEntityId());
		target.setPaymentOption(source.getPaymentOption().getCode());
		target.setCheckoutSummaryRequired(source.getCheckoutSummaryRequired());
		target.setIsThreeDSEnabled(source.getIsThreedsEnabled());
		if (source.getIsThreedsEnabled().booleanValue())
		{
			populate3DS(source, target);
		}

		final List<ACIPaymentMethodsData> paymentMethodsList = new ArrayList<ACIPaymentMethodsData>();
		for (final ACIPaymentMethodsModel aciPaymentMethod : source.getAllowedPaymentTypes())
		{
			final ACIPaymentMethodsData aciPaymentMethodsData = new ACIPaymentMethodsData();
			aciPaymentMethodsData.setCode(aciPaymentMethod.getCode());
			aciPaymentMethodsData.setName(aciPaymentMethod.getName());
			aciPaymentMethodsData.setDescription(aciPaymentMethod.getDescription());
			aciPaymentMethodsData.setAllowRegstration(aciPaymentMethod.getAllowRegstration());
			if (aciPaymentMethod.getSyncType() != null)
			{
				aciPaymentMethodsData.setSync(aciPaymentMethod.getSyncType().getCode());
			}
			paymentMethodsList.add(aciPaymentMethodsData);
		}
		target.setAllowedPaymentMethods(paymentMethodsList);
	}

	/**
	 *
	 */
	private void populate3DS(final ACIConfigModel source, final ACIConfigData target)
	{
		target.setDsTransactionId(source.getDsTransactionId());

		if (source.getThreeDSChallengeIndicator() != null)
		{
			target.setThreeDSChallengeIndicator(
					ACIUtilConstants.threeDSChallengeIndicatorMap.get(source.getThreeDSChallengeIndicator().getCode()));
		}
		if (source.getThreeDSchallengeMandatedInd() != null)
		{
			target.setThreeDSchallengeMandatedInd(source.getThreeDSchallengeMandatedInd().getCode());
		}
		if (source.getThreeDSAuthentication() != null)
		{
			target.setThreeDSAuthentication(
					ACIUtilConstants.threeDSAuthenticationMap.get(source.getThreeDSAuthentication().getCode()));
		}
		if (source.getThreeDSExemptionFlag() != null)
		{
			target.setThreeDSExemptionFlag(ACIUtilConstants.threeDSExemptionFlagMap.get(source.getThreeDSExemptionFlag().getCode()));
		}
		target.setThreeDSExemptionFlag(source.getThreeDSVersion());
		target.setTransactionStatusReason(source.getTransactionStatusReason());
		target.setAcsTransactionId(source.getAcsTransactionId());


	}
}