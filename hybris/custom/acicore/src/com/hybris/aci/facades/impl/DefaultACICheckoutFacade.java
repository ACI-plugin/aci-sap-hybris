/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades.impl;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;

import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.facades.ACICheckoutFacade;
import com.hybris.aci.facades.ACIPaymentFacade;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.httpclient.models.Card;
import com.hybris.aci.service.ACIOrderService;
import com.hybris.aci.service.ACITransactionService;
import com.hybris.aci.util.ACIUtil;
import com.hybris.aci.util.LogHelper;


public class DefaultACICheckoutFacade implements ACICheckoutFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultACICheckoutFacade.class);

	private CartService cartService;
	private ACIPaymentFacade aciPaymentFacade;
	private CommonI18NService commonI18NService;
	private Converter<AddressData, AddressModel> aciAddressReverseConverter;
	private CheckoutFacade checkoutFacade;
	private ACIOrderService aciOrderService;
	private ACITransactionService aciTransactionService;
	private ModelService modelService;

	private static final String ACI = "ACI";

	@Override
	public boolean hasNoPaymentInfo()
	{
		final CartModel cartModel = cartService.getSessionCart();
		return cartModel == null || cartModel.getPaymentInfo() == null;
	}

	/**
	 * Process payment address saves to Cart during payment address step
	 *
	 * @param addressData
	 */
	@Override
	public void processPaymentAddress(final AddressData addressData)
	{
		LogHelper.debugLog(LOG, " processPayment .. ");
		final CartModel cart = cartService.getSessionCart();
		PaymentInfoModel aciPaymentInfo = null;
		if (cart.getPaymentInfo() == null)
		{
			aciPaymentInfo = modelService.create(PaymentInfoModel.class);
			final String aciPaymentCode = ACI + cart.getCode();
			aciPaymentInfo.setCode(aciPaymentCode);
			aciPaymentInfo.setUser(userService.getCurrentUser());
		}
		else
		{
			aciPaymentInfo = cart.getPaymentInfo();
		}

		if (addressData != null)
		{
			final AddressModel billingAddress = aciAddressReverseConverter.convert(addressData);
			billingAddress.setOwner(aciPaymentInfo);
			aciPaymentInfo.setBillingAddress(billingAddress);
		}
		cart.setPaymentInfo(aciPaymentInfo);
		modelService.saveAll();
		cartService.setSessionCart(cart);
	}

	/**
	 * Updates the ACI Session id to Cart for fulture use
	 *
	 * @param sessionId
	 */
	@Override
	public void updateCartWithSessionId(final String sessionId)
	{
		LogHelper.debugLog(LOG, " processPayment .. ");
		final CartModel cart = cartService.getSessionCart();
		cart.setAciCheckoutId(sessionId);

		modelService.saveAll();
		cartService.setSessionCart(cart);
	}

	/**
	 * Authorizes order by calling ACI API
	 *
	 * @param resourcePath
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse authorizeOrder(final String resourcePath)
	{
		LOG.info("Authorization - Start");
		final CartModel cart = cartService.getSessionCart();
		final ACIPaymentProcessResponse aciAuthorizationResponse = aciPaymentFacade.getAciStatus(resourcePath);
		ACIUtil.setAciTransactionStatus(aciAuthorizationResponse, cart);
		if (aciAuthorizationResponse.isOk())
		{
			updateCart(cart, aciAuthorizationResponse);

			createAuthorizationTransactions(cart, aciAuthorizationResponse);

			LOG.info("Authorization - Successfull, Updarted Cart and created Transactions");
		}

		return aciAuthorizationResponse;

	}

	/**
	 * Update the cart wit the authorization response from ACI
	 *
	 * @param cart
	 * @param aciAuthorizationResponse
	 */
	private void updateCart(final AbstractOrderModel cart, final ACIPaymentProcessResponse aciAuthorizationResponse)
	{
		cart.setAciPaymentId(aciAuthorizationResponse.getId());
		cart.setAciMerchantTransactionId(aciAuthorizationResponse.getMerchantTransactionId());
		cart.setAciPaymentType(aciAuthorizationResponse.getPaymentType());

		final String aciPaymentStatus = ACIUtil.processResultCode(aciAuthorizationResponse.getResult().getCode());
		cart.setAciPaymentStatus(ACIUtilConstants.aciPaymentStatusMap.get(aciPaymentStatus));
		updatePaymentInfo(cart, aciAuthorizationResponse);

		modelService.saveAll();
	}

	private void updatePaymentInfo(final AbstractOrderModel order, final ACIPaymentProcessResponse aciAuthorizationResponse)
	{
		LogHelper.debugLog(LOG, "ACICheckoutFacade - updatePaymentInfo - Start");
		if (order == null)
		{
			LOG.error("Order is null");
			return;
		}
		final PaymentInfoModel paymentInfo = order.getPaymentInfo();
		paymentInfo.setAciPaymentBrand(aciAuthorizationResponse.getPaymentBrand());
		paymentInfo.setAciPaymentType(aciAuthorizationResponse.getPaymentType());
		paymentInfo.setAciPaymentId(aciAuthorizationResponse.getId());
		paymentInfo.setAciRegistrationId(aciAuthorizationResponse.getRegistrationId());
		if (aciAuthorizationResponse.getCard() != null)
		{
			LogHelper.debugLog(LOG, "ACICheckoutFacade - updatePaymentInfo - Card Payment Update");
			final Card card = aciAuthorizationResponse.getCard();
			paymentInfo.setAciCardBin(card.getBin());
			paymentInfo.setAciCardNumber(ACIUtilConstants.MASK + card.getLast4Digits());
			paymentInfo.setAciCardHolder(card.getHolder());
			paymentInfo.setAciCardExpiryMonth(card.getExpiryMonth());
			paymentInfo.setAciCardExpiryYear(card.getExpiryYear());
			final CustomerModel currentCustomer = (CustomerModel) cartService.getSessionCart().getUser();
			LogHelper.debugLog(LOG, "aciAuthorizationResponse.getCustomParameters().getSHOPPER_savedCard() ::: "
					+ aciAuthorizationResponse.getCustomParameters().getSHOPPER_savedCard());
			if (!CustomerType.GUEST.equals(currentCustomer.getType())
					&& aciAuthorizationResponse.getCustomParameters().getSHOPPER_savedCard() != null
					&& aciAuthorizationResponse.getCustomParameters().getSHOPPER_savedCard().equalsIgnoreCase("true"))
			{
				paymentInfo.setAciRegistrationId(aciAuthorizationResponse.getRegistrationId());
				paymentInfo.setSaved(true);
			}
		}
		LOG.info("Payment Info Updated Successfully. ");
		LogHelper.debugLog(LOG, "ACICheckoutFacade - updatePaymentInfo - End");

	}

	/**
	 * Creates authorization payment transactions
	 *
	 * @param abstractOrderModel
	 * @param aciAuthorizationResponse
	 * @return
	 */
	public PaymentTransactionModel createAuthorizationTransactions(final AbstractOrderModel abstractOrderModel,
			final ACIPaymentProcessResponse aciAuthorizationResponse)
	{
		//First save the transactions to the CartModel < AbstractOrderModel
		final PaymentTransactionModel paymentTransactionModel = aciTransactionService
				.createPaymentTransaction(aciAuthorizationResponse, abstractOrderModel);

		modelService.save(paymentTransactionModel);
		final PaymentTransactionEntryModel authorisedTransaction = aciTransactionService
				.createPaymentTransactionEntryModel(paymentTransactionModel, aciAuthorizationResponse);

		LOG.info("Saving AUTH transaction entry with psp reference: " + aciAuthorizationResponse.getId());
		modelService.save(authorisedTransaction);

		modelService.refresh(paymentTransactionModel); //refresh is needed by order-process

		return paymentTransactionModel;
	}


	/**
	 * Order placement
	 *
	 * @param aciAuthorizationResponse
	 * @return
	 * @throws InvalidCartException
	 */
	@Override
	public OrderData placeorder(final ACIPaymentProcessResponse aciAuthorizationResponse) throws InvalidCartException
	{
		LogHelper.debugLog(LOG, "ACICheckoutFacade - placeorder - Start");
		final OrderData orderData = checkoutFacade.placeOrder();
		final OrderModel order = aciOrderService.findOrderModel(orderData.getCode());
		aciTransactionService.saveTransactionResponse(aciAuthorizationResponse, order);
		LOG.info("Order Place successfully");
		return orderData;
	}


	/**
	 * @return the aciPaymentFacade
	 */
	public ACIPaymentFacade getAciPaymentFacade()
	{
		return aciPaymentFacade;
	}

	/**
	 * @param aciPaymentFacade
	 *           the aciPaymentFacade to set
	 */
	public void setAciPaymentFacade(final ACIPaymentFacade aciPaymentFacade)
	{
		this.aciPaymentFacade = aciPaymentFacade;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the checkoutFacade
	 */
	public CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}

	/**
	 * @param checkoutFacade
	 *           the checkoutFacade to set
	 */
	public void setCheckoutFacade(final CheckoutFacade checkoutFacade)
	{
		this.checkoutFacade = checkoutFacade;
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
	 * @return the aciTransactionService
	 */
	public ACITransactionService getAciTransactionService()
	{
		return aciTransactionService;
	}

	/**
	 * @param aciTransactionService
	 *           the aciTransactionService to set
	 */
	public void setAciTransactionService(final ACITransactionService aciTransactionService)
	{
		this.aciTransactionService = aciTransactionService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	private UserService userService;

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the aciAddressReverseConverter
	 */
	public Converter<AddressData, AddressModel> getAciAddressReverseConverter()
	{
		return aciAddressReverseConverter;
	}

	/**
	 * @param aciAddressReverseConverter
	 *           the aciAddressReverseConverter to set
	 */
	public void setAciAddressReverseConverter(final Converter<AddressData, AddressModel> aciAddressReverseConverter)
	{
		this.aciAddressReverseConverter = aciAddressReverseConverter;
	}

}
