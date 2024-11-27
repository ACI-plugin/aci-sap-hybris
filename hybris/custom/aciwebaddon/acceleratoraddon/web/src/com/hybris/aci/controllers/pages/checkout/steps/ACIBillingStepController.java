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
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hybris.aci.controllers.AciwebaddonControllerConstants;
import com.hybris.aci.facades.ACICheckoutFacade;
import com.hybris.aci.facades.ACIConfigFacade;
import com.hybris.aci.facades.ACIPaymentFacade;
import com.hybris.aci.forms.ACIPaymentDetailsForm;
import com.hybris.aci.util.LogHelper;


@Controller
@RequestMapping(value = "/checkout/multi/aci/billing")
public class ACIBillingStepController extends AbstractCheckoutStepController
{

	private static final String BILLING_ADDRESS = "billing-address";
	private static final String CART_DATA_ATTR = "cartData";
	private static final Logger LOGGER = Logger.getLogger(ACIBillingStepController.class);

	@Resource(name = "aciPaymentFacade")
	private ACIPaymentFacade aciPaymentFacade;

	@Resource(name = "aciCheckoutFacade")
	private ACICheckoutFacade aciCheckoutFacade;

	@Resource(name = "aciConfigFacade")
	private ACIConfigFacade aciConfigFacade;

	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return getCheckoutFacade().getBillingCountries();
	}

	@Override
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = BILLING_ADDRESS)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getCheckoutFacade().setDeliveryModeIfAvailable();
		setupAddPaymentPage(model);

		setCheckoutStepLinksForModel(model, getCheckoutStep());

		final ACIPaymentDetailsForm aciPaymentDetailsForm = new ACIPaymentDetailsForm();
		setupACIPostPage(aciPaymentDetailsForm, model);
		return AciwebaddonControllerConstants.Views.Pages.MultiStepCheckout.AciPaymentMethodPage;



	}

	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}

	/**
	 * @param sopPaymentDetailsForm
	 * @param model
	 */
	private void setupACIPostPage(final ACIPaymentDetailsForm aciPaymentDetailsForm, final Model model)
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("aciPaymentDetailsForm", new ACIPaymentDetailsForm());
		model.addAttribute(CART_DATA_ATTR, cartData);
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		if (StringUtils.isNotBlank(aciPaymentDetailsForm.getBillTo_country()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(aciPaymentDetailsForm.getBillTo_country()));
			model.addAttribute("country", aciPaymentDetailsForm.getBillTo_country());
		}

	}

	@RequestMapping(value = "/process-address", method = RequestMethod.POST)
	@RequireHardLogIn
	public String processstandalone(@Valid
	final ACIPaymentDetailsForm aciPaymentDetailsForm, final BindingResult bindingResult, final Model model,
			final HttpSession httpSession, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		LogHelper.debugLog(LOGGER, "Going to process the payment address.. ");

		AddressData addressData;

		if (aciPaymentDetailsForm.isUseDeliveryAddress())
		{
			LogHelper.debugLog(LOGGER, "using delivery address as payment address. ");
			addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();

			if (addressData == null)
			{
				GlobalMessages.addErrorMessage(model,
						"checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg");
				return AciwebaddonControllerConstants.Views.Pages.MultiStepCheckout.AciPaymentMethodPage;
			}

			if (addressData.getEmail() == null && aciPaymentDetailsForm.getBillTo_email() != null)
			{
				addressData.setEmail(aciPaymentDetailsForm.getBillTo_email());
			}

			addressData.setBillingAddress(true); // mark this as billing address
		}
		else
		{
			addressData = new AddressData();
			fillInAddressData(addressData, aciPaymentDetailsForm);
		}

		aciCheckoutFacade.processPaymentAddress(addressData);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute(CART_DATA_ATTR, cartData);

		return getCheckoutStep().nextStep();
	}


	protected void fillInAddressData(final AddressData addressData, final ACIPaymentDetailsForm paymentDetailsForm)
	{
		if (paymentDetailsForm != null)
		{
			LogHelper.debugLog(LOGGER, "inside fill in address ");
			addressData.setFirstName(paymentDetailsForm.getBillTo_email());
			if (StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_titleCode()))
			{
				addressData.setTitleCode(paymentDetailsForm.getBillTo_titleCode());
			}
			addressData.setFirstName(paymentDetailsForm.getBillTo_firstName());
			addressData.setLastName(paymentDetailsForm.getBillTo_lastName());
			addressData.setLine1(paymentDetailsForm.getBillTo_street1());
			addressData.setLine2(paymentDetailsForm.getBillTo_street2());
			addressData.setTown(paymentDetailsForm.getBillTo_city());
			addressData.setPostalCode(paymentDetailsForm.getBillTo_postalCode());
			addressData.setEmail(paymentDetailsForm.getBillTo_email());

			if (StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_country()))
			{
				addressData.setCountry(getI18NFacade().getCountryForIsocode(paymentDetailsForm.getBillTo_country()));
			}
			if (StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_country())
					&& StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_state()))
			{
				final String regionIso = paymentDetailsForm.getBillTo_country() + "-" + paymentDetailsForm.getBillTo_state();
				addressData.setRegion(getI18NFacade().getRegion(paymentDetailsForm.getBillTo_country(), regionIso));

			}
			addressData.setBillingAddress(true);
		}
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(BILLING_ADDRESS);
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

}
