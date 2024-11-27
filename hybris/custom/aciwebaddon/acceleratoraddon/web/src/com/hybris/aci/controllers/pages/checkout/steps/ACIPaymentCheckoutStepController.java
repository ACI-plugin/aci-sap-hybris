/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.controllers.pages.checkout.steps;


import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
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
import com.hybris.aci.util.ACIUtil;
import com.hybris.aci.util.LogHelper;


@Controller
@RequestMapping(value = "/checkout/multi/aci")
public class ACIPaymentCheckoutStepController extends AbstractCheckoutStepController
{

	protected static final Map<String, String> CYBERSOURCE_SOP_CARD_TYPES = new HashMap<>();

	private static final String ACI_PAYMENT = "aci-payment";
	private static final String CART_DATA_ATTR = "cartData";
	private static final String IS_ACI_ACTIVE = "is_aci_active";

	private static final String ACI_CHECKOUTID = "aci_checkoutid";
	private static final String CART_URL = "cartUrl";
	private static final String ACI_SCRIPT_URL = "aci_script_url";
	private static final String MERCHANT_PLACEORDER_URL = "aci.merchant.url.placeorder";
	private static final String MERCHANT_CART_URL = "aci.merchant.url.cart";
	private static final Logger LOGGER = Logger.getLogger(ACIPaymentCheckoutStepController.class);
	private static final String MERCHANT_PROCESS_ACI = "aci.merchant.url.processaci";
	private static final String PROCESS_ACI_PAYMENT_URL = "process_aci_payment_url";
	private static final String ACI_REG_ALLOWED_CARDS = "aci_reg_allowed_cards";
	private static final String ACI_SELECTED_BRAND = "aci_selected_brand";
	public static final String ACI_SUMMARY_ACTION = "aci_summary_action";
	private static final String FLOWTYPE = "flow_type";
	private static final String SUMMARY = "SUMMARY";
	private static final String NOSUMMARY = "NOSUMMARY";
	private static final String USESUMAMRY = "useSummary";
	private static final String PAYMENT_SELECTED = "payment_selected";

	@Resource(name = "aciPaymentFacade")
	private ACIPaymentFacade aciPaymentFacade;

	@Resource(name = "aciCheckoutFacade")
	private ACICheckoutFacade aciCheckoutFacade;

	@Resource(name = "aciConfigFacade")
	private ACIConfigFacade aciConfigFacade;

	//@Override
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = ACI_PAYMENT)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes, final HttpServletRequest request,
			final HttpSession httpSession) throws CMSItemNotFoundException
	{
		final ACIConfigData aciConfig = aciConfigFacade.getACIConfig();
		if (aciConfig != null && aciConfig.getActive())
		{

			final String ipAddress = request.getLocalAddr();

			final CartData cartData = getCheckoutFacade().getCheckoutCart();
			final ACIPaymentProcessResponse response = aciPaymentFacade.getAciCheckoutID(true, null, ipAddress, null);
			if (!response.isOk())
			{
				httpSession.removeAttribute(ACI_CHECKOUTID);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
						"checkout.multi.paymentMethod.addPaymentDetails.generalError", new String[] {});
				return getCheckoutStep().previousStep();
			}

			String aciCheckoutId = null;
			if (StringUtils.isNotEmpty(response.getId()))
			{
				aciCheckoutId = response.getId();
				httpSession.setAttribute(ACI_CHECKOUTID, aciCheckoutId);
				aciCheckoutFacade.updateCartWithSessionId((String) httpSession.getAttribute(ACI_CHECKOUTID));

			}


			setpAciStandalonePage(aciCheckoutId, aciConfig, cartData, model);

			model.addAttribute("deliveryMethods", getCheckoutFacade().getSupportedDeliveryModes());
			this.prepareDataForPage(model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
			model.addAttribute(WebConstants.BREADCRUMBS_KEY,
					getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
			model.addAttribute("metaRobots", "noindex,nofollow");
			setCheckoutStepLinksForModel(model, getCheckoutStep(ACI_PAYMENT));
		}
		return AciwebaddonControllerConstants.Views.Pages.MultiStepCheckout.AciStandalonePaymentPage;
	}

	/**
	 * @param aciCheckoutId
	 * @param aciConfig
	 * @param cartData
	 * @param model
	 */
	private void setpAciStandalonePage(final String aciCheckoutId, final ACIConfigData aciConfig, final CartData cartData,
			final Model model)
	{
		model.addAttribute(ACI_CHECKOUTID, aciCheckoutId);
		model.addAttribute(IS_ACI_ACTIVE, Boolean.TRUE);
		model.addAttribute(USESUMAMRY, aciConfig.getCheckoutSummaryRequired());
		final String allowedCards = ACIUtil.getRegistrationAllowedCards(aciConfig);

		model.addAttribute(ACI_SELECTED_BRAND, ACIUtil.getBrands(aciConfig));
		model.addAttribute(ACI_REG_ALLOWED_CARDS, allowedCards);
		model.addAttribute(ACI_SCRIPT_URL, aciConfigFacade.getACIScriptUrl(aciCheckoutId));
		model.addAttribute(ACIUtilConstants.SHOPPER_RESULT_URL, aciConfigFacade.getMerchantUrl(MERCHANT_PLACEORDER_URL));
		model.addAttribute(CART_URL, aciConfigFacade.getMerchantUrl(MERCHANT_CART_URL));
		model.addAttribute(PROCESS_ACI_PAYMENT_URL, aciConfigFacade.getMerchantUrl(MERCHANT_PROCESS_ACI));
		model.addAttribute(FLOWTYPE, aciConfig.getCheckoutSummaryRequired() ? SUMMARY : NOSUMMARY);
		model.addAttribute(CART_DATA_ATTR, cartData);

	}

	@RequestMapping(value = "/process", method = RequestMethod.GET)
	@RequireHardLogIn
	public String process(final Model model, final RedirectAttributes redirectAttributes, final HttpSession httpSession)
			throws CMSItemNotFoundException
	{
		LogHelper.debugLog(LOGGER, "Going to process the payment .. ");

		setCheckoutStepLinksForModel(model, getCheckoutStep());
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute(CART_DATA_ATTR, cartData);
		redirectAttributes.addFlashAttribute(PAYMENT_SELECTED, "Y");

		return getCheckoutStep().nextStep();
	}

	@RequestMapping(value = "/redirecttoACI", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateQuoteCheckoutStep
	public String redirecttoACI(final Model model, final HttpSession httpSession)
			throws CMSItemNotFoundException,
			CommerceCartModificationException
	{

		final String aciCheckoutId = (String) httpSession.getAttribute(ACI_CHECKOUTID);
		final StringBuilder sb = new StringBuilder(aciConfigFacade.getBaseUrl());
		sb.append(ACIUtilConstants.SLASH).append(aciConfigFacade.getAPiVersion()).append(ACIUtilConstants.SLASH)
				.append(ACIUtilConstants.CHECKOUTS).append(ACIUtilConstants.SLASH).append(aciCheckoutId)
				.append(ACIUtilConstants.SLASH).append(ACIUtilConstants.PAYMENT);
		model.addAttribute(ACI_CHECKOUTID, aciCheckoutId);
		model.addAttribute(ACI_SUMMARY_ACTION, sb.toString());
		LOGGER.info("in aci summary redirect :: " + aciCheckoutId);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return AciwebaddonControllerConstants.Views.Pages.MultiStepCheckout.AciRedirect;
	} /*
	   * (non-Javadoc)
	   *
	   * @see de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.CheckoutStepController#
	   * enterStep (org.springframework.ui.Model, org.springframework.web.servlet.mvc.support.RedirectAttributes)
	   */

	@Override
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.CheckoutStepController#back(org
	 * .springframework.web.servlet.mvc.support.RedirectAttributes)
	 */
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.CheckoutStepController#next(org
	 * .springframework.web.servlet.mvc.support.RedirectAttributes)
	 */
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(ACI_PAYMENT);
	}


}
