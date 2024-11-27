/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCartPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.core.enums.QuoteState;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hybris.aci.controllers.AciwebaddonControllerConstants;
import com.hybris.aci.util.LogHelper;


/**
 * Controller for cart page
 */
@Controller
@RequestMapping(value = "/cart/aci")
public class ACICartPageController extends AbstractCartPageController
{
	public static final String SHOW_CHECKOUT_STRATEGY_OPTIONS = "storefront.show.checkout.flows";
	public static final String ERROR_MSG_TYPE = "errorMsg";
	public static final String SUCCESSFUL_MODIFICATION_CODE = "success";
	public static final String VOUCHER_FORM = "voucherForm";
	public static final String SITE_QUOTES_ENABLED = "site.quotes.enabled.";
	private static final String REDIRECT_QUOTE_EDIT_URL = REDIRECT_PREFIX + "/quote/%s/edit/";
	private static final String REDIRECT_QUOTE_VIEW_URL = REDIRECT_PREFIX + "/my-account/my-quotes/%s/";

	public static final String ACI_CHECKOUTID = "aci_checkoutid";

	private static final Logger LOG = Logger.getLogger(ACICartPageController.class);

	@ModelAttribute("showCheckoutStrategies")
	public boolean isCheckoutStrategyVisible()
	{
		return getSiteConfigService().getBoolean(SHOW_CHECKOUT_STRATEGY_OPTIONS, false);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showCart(final Model model, final HttpServletRequest request,
			@RequestParam(value = "acisessiontimeout", required = false)
			final boolean acisessiontimeout) throws CMSItemNotFoundException
	{

		LogHelper.debugLog(LOG, "Entering aci cart - session time out occurred");
		model.addAttribute("acisessiontimeout", acisessiontimeout);
		if (acisessiontimeout)
		{
			request.getSession().removeAttribute(ACI_CHECKOUTID);
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.aci.sessiontimeout");
		}
		return prepareCartUrl(model);
	}

	protected String prepareCartUrl(final Model model) throws CMSItemNotFoundException
	{
		final Optional<String> quoteEditUrl = getQuoteUrl();
		if (quoteEditUrl.isPresent())
		{
			return quoteEditUrl.get();
		}
		else
		{
			prepareDataForPage(model);

			return AciwebaddonControllerConstants.Views.Pages.Cart.CartPage;
		}
	}

	protected Optional<String> getQuoteUrl()
	{
		final QuoteData quoteData = getCartFacade().getSessionCart().getQuoteData();

		if (quoteData != null)
		{
			if (QuoteState.BUYER_OFFER.equals(quoteData.getState()))
			{
				return Optional.of(String.format(REDIRECT_QUOTE_VIEW_URL, urlEncode(quoteData.getCode())));
			}
			else
			{
				return Optional.of(String.format(REDIRECT_QUOTE_EDIT_URL, urlEncode(quoteData.getCode())));
			}
		}
		else
		{
			return Optional.empty();
		}
	}


}
