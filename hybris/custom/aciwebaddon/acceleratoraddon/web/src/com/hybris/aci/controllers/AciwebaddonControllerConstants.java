/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.controllers;

/**
 */
public interface AciwebaddonControllerConstants
{
	// implement here controller constants used by this extension



	static String ADDON_PREFIX = "addon:/aciwebaddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{

		interface Pages
		{

			interface MultiStepCheckout
			{
				String AddPaymentMethod = "pages/checkout/multi/addPaymentMethodPage";
				String AciCheckoutSummaryPage = ADDON_PREFIX + "pages/checkout/multi/aciCheckoutSummaryPage"; // NOSONAR
				String AciRedirect = ADDON_PREFIX + "pages/checkout/multi/aciRedirect";
				String AciTemp = ADDON_PREFIX + "pages/checkout/multi/aciTemp";
				String AciStandalonePaymentPage = ADDON_PREFIX + "pages/checkout/multi/aciStandalonePaymentPage";
				String AciPaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/aciPaymentMethod";
				String AciStandalonePaymentMethodPage = ADDON_PREFIX + "pages/checkout/multi/aciStandalonePaymentMethod";
			}

			interface Checkout
			{
				String KP_REDIRECT_CART = "/cart";
			}

			interface Cart // NOSONAR
			{
				String CartPage = "pages/cart/cartPage"; // NOSONAR
			}
		}

		interface Fragments
		{
			interface Checkout
			{
				String ACIWidget = ADDON_PREFIX + "fragments/checkout/aciSelectedPayment";
			}
		}
	}
}
