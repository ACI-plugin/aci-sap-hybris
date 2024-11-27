/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.facades.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aci.payment.data.ACIConfigData;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.facades.ACIConfigFacade;
import com.hybris.aci.facades.ACIPaymentFacade;
import com.hybris.aci.httpclient.CheckoutOrder;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.populator.DataPopulator;
import com.hybris.aci.service.ACIAccountService;
import com.hybris.aci.service.ACIPaymentService;
import com.hybris.aci.util.ACIUtil;
import com.hybris.aci.util.LogHelper;


public class DefaultACIPaymentFacade implements ACIPaymentFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultACIPaymentFacade.class);


	ACIConfigFacade aciConfigFacade;
	SiteConfigService siteConfigService;
	CartService cartService;
	CartFacade cartFacade;
	CustomerEmailResolutionService customerEmailResolutionService;
	ACIPaymentService aciPaymentService;
	ACIAccountService aciAccountService;


	/**
	 *
	 * This method does the prepare checkout in ACI and returns SessionId.
	 *
	 * @param isCheckout
	 * @param checkoutId
	 * @param ipAddress
	 * @param customerData
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse getAciCheckoutID(final boolean isCheckout, final String checkoutId, final String ipAddress,
			final CustomerData customerData)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentFacade getAciCheckoutID - Start");

		final CheckoutOrder aciCheckoutOrder = new CheckoutOrder(aciConfigFacade.getBaseUrl(), aciConfigFacade.getAPiVersion());
		final Map<String, String> m = new HashMap<String, String>();
		final ACIConfigData aciConfig = aciConfigFacade.getACIConfig();
		final DataPopulator dp = new DataPopulator();
		dp.populateAuthData(m, aciConfig.getEntityId());
		final String testMode = siteConfigService.getProperty(ACIUtilConstants.ACI_TEST_MODE);
		final String forceResultCode = siteConfigService.getProperty(ACIUtilConstants.ACI_FORCE_RESULT_CODE);
		dp.populateCustomData(testMode, forceResultCode, aciConfig, m);
		dp.populateMerchantData(m);
		dp.populateRiskData("test", m);

		boolean createRegisrtation = false;
		if (customerData != null)
		{
			createRegisrtation = true;
			dp.populateCustomerForRegistration(customerData, m, ipAddress);
		}
		if (isCheckout)
		{

			final CustomerModel currentCustomer = (CustomerModel) cartService.getSessionCart().getUser();
			final String email = customerEmailResolutionService.getEmailForCustomer(currentCustomer);
			final CartData cartData = cartFacade.getSessionCart();

			final AbstractOrderModel orderModel = cartService.getSessionCart();
			BigDecimal totalAmount = null;
			if (cartData.isNet())
			{
				totalAmount = cartData.getTotalPriceWithTax().getValue();
			}
			else
			{
				totalAmount = cartData.getTotalPrice().getValue();
			}
			dp.populateBaseData(orderModel, totalAmount, m, createRegisrtation, aciConfig.getPaymentOption());
			dp.populateBillingAddress(orderModel, m);
			dp.populateShippingAddress(orderModel, m, email);
			dp.populateCustomer(orderModel, m, currentCustomer);
			dp.populateCartData(orderModel, m);


			if (!CustomerType.GUEST.equals(currentCustomer.getType()))
			{
				dp.populateRegistrationIds(aciAccountService.getAciCardPaymentInfos(currentCustomer),
						ACIUtil.getRegistrationAllowedCards(aciConfig), m);
			}
			dp.populateCustomerDetails(currentCustomer, m, ipAddress, email);
		}

		final ACIPaymentProcessResponse response = aciCheckoutOrder.prepareCheckout(m, aciConfig.getBearerToken(), checkoutId);
		setPrepareCheckoutResponseStatus(response);

		LOG.info("PrepareCheckout - response ok  - " + response.isOk());
		LOG.info("PrepareCheckout -  result description - " + response.getDescriptor());
		LOG.info("PrepareCheckout -  result code - " + response.getErrorCode());

		LogHelper.debugLog(LOG, "DefaultACIPaymentFacade getAciCheckoutID - End");

		return response;

	}

	private void setPrepareCheckoutResponseStatus(final ACIPaymentProcessResponse response)
	{
		if (ACIUtilConstants.prepareCheckoutStatuses.contains(response.getResult().getCode()))
		{
			response.setOk(true);
			response.setDescriptor(response.getResult().getDescription());
		}
		else
		{
			response.setOk(false);
			response.setErrorCode(response.getResult().getCode());
			response.setDescriptor(response.getResult().getDescription());
		}

	}

	/**
	 * This method returns the ACI Status with the given resource path returned from ACI
	 *
	 * @param resourcePath
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse getAciStatus(final String resourcePath)
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentFacade getAciStatus - Start");

		return aciPaymentService.getAciStatus(resourcePath);
	}

	/**
	 * This method handles place order failure scenario
	 *
	 * @return
	 */
	@Override
	public ACIPaymentProcessResponse handlePlaceOrderFailure()
	{
		LogHelper.debugLog(LOG, "DefaultACIPaymentFacade handlePlaceOrderFailure - Start");
		final CartModel cart = cartService.getSessionCart();
		ACIPaymentProcessResponse aciWebCallResponse = null;
		for (final PaymentTransactionModel transaction : cart.getPaymentTransactions())
		{

			PaymentTransactionEntryModel transactionEntry = null;
			final Iterator ite = transaction.getEntries().iterator();

			while (ite.hasNext())
			{
				final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) ite.next();
				if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
				{
					transactionEntry = pte;
					break;
				}
			}

			if (transactionEntry == null)
			{
				throw new AdapterException("Could not capture without authorization");
			}
			else
			{
				if (transactionEntry.getType().equals(PaymentTransactionType.CAPTURE))
				{
					LOG.info("DefaultACIPaymentFacade handlePlaceOrderFailure - Going for Refund");

					aciWebCallResponse = aciPaymentService.refund(cart, transactionEntry,
							String.format("%.2f", transactionEntry.getAmount()), transactionEntry.getCurrency().getIsocode());
				}
				else
				{
					LOG.info("DefaultACIPaymentFacade handlePlaceOrderFailure - Going for Reversal");
					aciWebCallResponse = aciPaymentService.reversal(cart, transactionEntry,
							String.format("%.2f", transactionEntry.getAmount()), transactionEntry.getCurrency().getIsocode());
				}
			}
		}
		return aciWebCallResponse;
	}



	/**
	 * @return the customerEmailResolutionService
	 */
	public CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	/**
	 * @param customerEmailResolutionService
	 *           the customerEmailResolutionService to set
	 */
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}


	/**
	 * @return the aciAccountService
	 */
	public ACIAccountService getAciAccountService()
	{
		return aciAccountService;
	}

	/**
	 * @param aciAccountService
	 *           the aciAccountService to set
	 */
	public void setAciAccountService(final ACIAccountService aciAccountService)
	{
		this.aciAccountService = aciAccountService;
	}

	/**
	 * @return the aciPaymentService
	 */
	public ACIPaymentService getAciPaymentService()
	{
		return aciPaymentService;
	}

	/**
	 * @param aciPaymentService
	 *           the aciPaymentService to set
	 */
	public void setAciPaymentService(final ACIPaymentService aciPaymentService)
	{
		this.aciPaymentService = aciPaymentService;
	}

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
	 * @return the aciConfigFacade
	 */
	public ACIConfigFacade getAciConfigFacade()
	{
		return aciConfigFacade;
	}

	/**
	 * @param aciConfigFacade
	 *           the aciConfigFacade to set
	 */
	public void setAciConfigFacade(final ACIConfigFacade aciConfigFacade)
	{
		this.aciConfigFacade = aciConfigFacade;
	}

	/**
	 * @return the siteConfigService
	 */
	public SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	/**
	 * @param siteConfigService
	 *           the siteConfigService to set
	 */
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}


	/**
	 * @return the cartFacade
	 */
	public CartFacade getCartFacade()
	{
		return cartFacade;
	}


	/**
	 * @param cartFacade
	 *           the cartFacade to set
	 */
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}


}
