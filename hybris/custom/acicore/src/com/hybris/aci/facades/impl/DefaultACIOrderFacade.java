/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades.impl;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.hybris.aci.facades.ACIOrderFacade;
import com.hybris.aci.service.ACIOrderService;


public class DefaultACIOrderFacade implements ACIOrderFacade
{
	ACIOrderService aciOrderService;
	private Converter aciOrderConverter;

	/**
	 * Gets order using payment ID
	 *
	 * @param paymentId
	 * @return
	 */
	@Override
	public OrderData getOrderByAciPaymentId(final String paymentId)
	{
		final OrderModel orderModel = aciOrderService.findOrderByAciPaymentId(paymentId);
		return (OrderData) aciOrderConverter.convert(orderModel);
	}

	/**
	 * @return the aciOrderService
	 */
	public ACIOrderService getAciOrderService()
	{
		return aciOrderService;
	}

	/**
	 * @param aciOrderService
	 *           the aciOrderService to set
	 */
	public void setAciOrderService(final ACIOrderService aciOrderService)
	{
		this.aciOrderService = aciOrderService;
	}

	/**
	 * @return the aciOrderConverter
	 */
	public Converter getAciOrderConverter()
	{
		return aciOrderConverter;
	}

	/**
	 * @param aciOrderConverter
	 *           the aciOrderConverter to set
	 */
	public void setAciOrderConverter(final Converter aciOrderConverter)
	{
		this.aciOrderConverter = aciOrderConverter;
	}

}
