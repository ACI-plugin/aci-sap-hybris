/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aci.payment.data.ACIConfigData;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.controllers.AciwebaddonControllerConstants;
import com.hybris.aci.facades.ACICheckoutFacade;
import com.hybris.aci.facades.ACIConfigFacade;
import com.hybris.aci.facades.ACIPaymentFacade;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.util.LogHelper;


@Controller
@RequestMapping(value = "/checkout/multi/summary/aci")
public class ACISummaryCheckoutStepController extends AbstractCheckoutStepController
{
	private static final Logger LOG = Logger.getLogger(ACISummaryCheckoutStepController.class);

	private static final String ACI_PAYMENT = "aci-payment";
	private static final String SUMMARY = "summary";
	public static final String ACI_CHECKOUTID = "aci_checkoutid";
	public static final String ACI_SUMMARY_ACTION = "aci_summary_action";
	private static final String PAYMENT_SELECTED = "payment_selected";
	private static final String TERMSCHECK = "termsCheck";

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;
	@Resource(name = "aciCheckoutFacade")
	private ACICheckoutFacade aciCheckoutFacade;
	@Resource(name = "aciPaymentFacade")
	private ACIPaymentFacade aciPaymentFacade;
	@Resource(name = "aciConfigFacade")
	private ACIConfigFacade aciConfigFacade;
	@Resource(name = "checkoutCustomerStrategy")
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@RequestMapping(value = "/redirect", method = RequestMethod.POST)
	@RequireHardLogIn
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = SUMMARY)
	public String redirect(final Model model, final RedirectAttributes redirectAttributes, final HttpSession httpSession,
			final HttpServletRequest request) throws CMSItemNotFoundException, // NOSONAR
			CommerceCartModificationException
	{

		if (isSummaryFlow() && request.getParameter(TERMSCHECK) == null
				|| !request.getParameter(TERMSCHECK).equalsIgnoreCase("true"))
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
			model.addAttribute(PAYMENT_SELECTED, "Y");
			return enterStep(model, redirectAttributes);
		}
		final String aciCheckoutId = (String) httpSession.getAttribute(ACI_CHECKOUTID);
		final StringBuilder sb = new StringBuilder(aciConfigFacade.getBaseUrl());
		sb.append(ACIUtilConstants.SLASH).append(aciConfigFacade.getAPiVersion()).append(ACIUtilConstants.SLASH)
				.append(ACIUtilConstants.CHECKOUTS).append(ACIUtilConstants.SLASH).append(aciCheckoutId)
				.append(ACIUtilConstants.SLASH).append(ACIUtilConstants.PAYMENT);
		model.addAttribute(ACI_CHECKOUTID, aciCheckoutId);
		model.addAttribute(ACI_SUMMARY_ACTION, sb.toString());
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return AciwebaddonControllerConstants.Views.Pages.MultiStepCheckout.AciRedirect;
	}


	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = SUMMARY)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, // NOSONAR
			CommerceCartModificationException
	{
		LogHelper.debugLog(LOG, "inside ACISummaryCheckoutStepController. enter ");
		if (isSummaryFlow() && !model.containsAttribute(PAYMENT_SELECTED))
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
					"checkout.paymentMethod.notSelected");
			return getCheckoutStep(ACI_PAYMENT).currentStep();
		}
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				final String productCode = entry.getProduct().getCode();
				final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode, Arrays.asList(
						ProductOption.BASIC, ProductOption.PRICE, ProductOption.VARIANT_MATRIX_BASE, ProductOption.PRICE_RANGE));
				entry.setProduct(product);
			}
		}

		model.addAttribute("cartData", cartData);
		model.addAttribute("allItems", cartData.getEntries());
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("deliveryMode", cartData.getDeliveryMode());
		model.addAttribute("paymentInfo", cartData.getPaymentInfo());

		// Only request the security code if the SubscriptionPciOption is set to Default.
		final boolean requestSecurityCode = CheckoutPciOptionEnum.DEFAULT
				.equals(getCheckoutFlowFacade().getSubscriptionPciOption());
		model.addAttribute("requestSecurityCode", Boolean.valueOf(requestSecurityCode));

		model.addAttribute(new PlaceOrderForm());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return AciwebaddonControllerConstants.Views.Pages.MultiStepCheckout.AciCheckoutSummaryPage;
	}

	@RequestMapping(value = "/placeorder", method = RequestMethod.GET)
	@PreValidateQuoteCheckoutStep
	@RequireHardLogIn
	public String placeOrder(final HttpServletRequest request, final Model model, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, // NOSONAR
			InvalidCartException, CommerceCartModificationException
	{

		LogHelper.debugLog(LOG, "Entering Place Order");
		if (validateOrderForm(request, model))
		{
			return enterStep(model, redirectModel);
		}

		if (validateCart(redirectModel))
		{
			return REDIRECT_PREFIX + "/cart";
		}




		final String ipAddress = request.getLocalAddr();
		String aciCheckoutId = null;
		if (request.getSession().getAttribute(ACI_CHECKOUTID) != null)
		{
			aciCheckoutId = (String) request.getSession().getAttribute(ACI_CHECKOUTID);
		}

		aciPaymentFacade.getAciCheckoutID(true, aciCheckoutId, ipAddress, null);



		final ACIPaymentProcessResponse aciAuthorizationResponse = aciCheckoutFacade
				.authorizeOrder(request.getParameter("resourcePath"));
		if (aciAuthorizationResponse.isOk())
		{
			try
			{
				final OrderData orderData = aciCheckoutFacade.placeorder(aciAuthorizationResponse);
				request.getSession().removeAttribute(ACI_CHECKOUTID);

				return redirectToOrderConfirmationPage(orderData);
			}
			catch (final Exception e)
			{
				LOG.error("Failed to place Order", e);
				GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			}
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"checkout.multi.paymentMethod.addPaymentDetails.generalError", new String[] {});

		}
		request.getSession().removeAttribute(ACI_CHECKOUTID);

		return redirectStep();

	}

	/**
	 * Validates the order form before to filter out invalid order states
	 *
	 * @param request
	 *           The spring form of the order being submitted
	 * @param model
	 *           A spring Model
	 * @return True if the order form is invalid and false if everything is valid.
	 */
	protected boolean validateOrderForm(final HttpServletRequest request, final Model model)
	{
		LogHelper.debugLog(LOG, " Entering validateOrderForm .. ");
		final String securityCode = request.getParameter("securityCode");
		boolean invalid = false;

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
			invalid = true;
		}

		if (getCheckoutFlowFacade().hasNoDeliveryMode())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
			invalid = true;
		}
		if (aciCheckoutFacade.hasNoPaymentInfo())
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
			invalid = true;
		}
		else
		{
			// Only require the Security Code to be entered on the summary page if the SubscriptionPciOption is set to Default.
			if (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade().getSubscriptionPciOption())
					&& StringUtils.isBlank(securityCode))
			{
				GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
				invalid = true;
			}
		}

		if (request.getParameter(TERMSCHECK) != null && request.getParameter(TERMSCHECK).equalsIgnoreCase("true"))
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
			invalid = true;
			return invalid;
		}
		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		if (!getCheckoutFacade().containsTaxValues())
		{
			LOG.error(String.format(
					"Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue",
					cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
			invalid = true;
		}

		if (!cartData.isCalculated())
		{
			LOG.error(
					String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	protected String redirectStep()
	{
		if (!isSummaryFlow())
		{
			return getCheckoutStep(ACI_PAYMENT).currentStep();
		}
		return getCheckoutStep(SUMMARY).previousStep();
	}

	protected CheckoutStep getCheckoutStep()
	{
		if (!isSummaryFlow())
		{
			return getCheckoutStep(ACI_PAYMENT);
		}
		return getCheckoutStep(SUMMARY);

	}

	protected boolean isSummaryFlow()
	{
		final ACIConfigData aciConfig = aciConfigFacade.getACIConfig();
		if (aciConfig != null)
		{
			return aciConfig.getCheckoutSummaryRequired();
		}
		return false;
	}


}
