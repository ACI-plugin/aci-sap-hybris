/*
 * ACI SAP Commerce Extension
 *
 */
package com.hybris.aci.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import java.util.Calendar;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aci.payment.data.ACIConfigData;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.facades.ACIAccountFacade;
import com.hybris.aci.facades.ACIConfigFacade;
import com.hybris.aci.facades.ACIPaymentFacade;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.util.ACIUtil;


/**
 * Controller for home page
 */
@Controller("aciAcoountPageController")
@RequestMapping("/my-account/aci-payment-details")
public class ACIAccountPaymentPageController extends AbstractSearchPageController
{
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";

	// Internal Redirects
	private static final String REDIRECT_TO_PAYMENT_INFO_PAGE = REDIRECT_PREFIX + "/my-account/aci-payment-details";

	// CMS Pages
	private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";
	private static final String PAYMENT_DETAILS_CMS_PAGE = "payment-details";

	private static final String ACI_SELECTED_BRAND = "aci_selected_brand";
	private static final String IS_ACI_ACTIVE = "is_aci_active";
	private static final String MERCHANT_SAVEPAYMENT_URL = "aci.merchant.url.savepayment";
	private static final String ADD = "add";
	private static final Object EDIT = "edit";

	@Resource(name = "aciConfigFacade")
	private ACIConfigFacade aciConfigFacade;

	@Resource(name = "aciAccountFacade")
	private ACIAccountFacade aciAccountFacade;

	@Resource(name = "aciPaymentFacade")
	private ACIPaymentFacade aciPaymentFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;



	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String paymentDetails(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		model.addAttribute(ACIUtilConstants.ADD_OR_EDIT, EDIT);
		model.addAttribute("customerData", customerFacade.getCurrentCustomer());
		model.addAttribute("paymentInfoData", aciAccountFacade.getACIPaymentInfos());
		storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.paymentDetails"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		final Calendar cal = Calendar.getInstance();
		model.addAttribute(ACIUtilConstants.CURRENT_MONTH, cal.get(Calendar.MONTH));
		model.addAttribute(ACIUtilConstants.CURRENT_YEAR, cal.get(Calendar.YEAR));

		return getViewForPage(model);
	}

	@RequestMapping(value = "/add-widget", method = RequestMethod.GET)
	@RequireHardLogIn
	public String displayACIWidget(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		final ACIConfigData aciConfig = aciConfigFacade.getACIConfig();
		if (aciConfig != null && aciConfig.getActive())
		{
			final String ipAddress = request.getRemoteAddr();
			model.addAttribute(IS_ACI_ACTIVE, Boolean.TRUE);
			final CustomerData customerData = customerFacade.getCurrentCustomer();

			final ACIPaymentProcessResponse response = aciPaymentFacade.getAciCheckoutID(false, null, ipAddress, customerData);
			if (!response.isOk())
			{
				GlobalMessages.addErrorMessage(model, "aci.payment.general.error");
			}
			else
			{
				model.addAttribute(ACIUtilConstants.ACI_SCRIPT_URL,
						aciConfigFacade.getACIScriptUrl(response.getId()) + ACIUtilConstants.SLASH + ACIUtilConstants.REGISTRATION);

				model.addAttribute(ACI_SELECTED_BRAND, ACIUtil.getRegistrationAllowedCards(aciConfig));
				model.addAttribute(ACIUtilConstants.SHOPPER_RESULT_URL, aciConfigFacade.getMerchantUrl(MERCHANT_SAVEPAYMENT_URL));
			}

		}

		model.addAttribute(ACIUtilConstants.ADD_OR_EDIT, ADD);
		storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs("text.account.paymentDetails"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addPaymentDetails(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		final String resourcePath = request.getParameter("resourcePath");
		aciAccountFacade.createAciPaymentInfo(resourcePath);

		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	@RequestMapping(value = "/set-default-payment-details", method = RequestMethod.POST)
	@RequireHardLogIn
	public String setDefaultPaymentDetails(@RequestParam
	final String paymentInfoId)
	{
		CCPaymentInfoData paymentInfoData = null;
		if (StringUtils.isNotBlank(paymentInfoId))
		{
			paymentInfoData = userFacade.getCCPaymentInfoForCode(paymentInfoId);
		}
		userFacade.setDefaultPaymentInfo(paymentInfoData);
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	@RequestMapping(value = "/remove-payment-method", method = RequestMethod.POST)
	@RequireHardLogIn
	public String removePaymentMethod(@RequestParam(value = "paymentInfoId")
	final String paymentMethodId, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		aciAccountFacade.unlinkACIPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart.removed");
		return REDIRECT_TO_PAYMENT_INFO_PAGE;
	}

	/**
	 * @return the aciConfigFacade
	 */
	public ACIConfigFacade getAciConfigFacade()
	{
		return aciConfigFacade;
	}

	/**
	 * @return the aciAccountFacade
	 */
	public ACIAccountFacade getAciAccountFacade()
	{
		return aciAccountFacade;
	}

	/**
	 * @return the aciPaymentFacade
	 */
	public ACIPaymentFacade getAciPaymentFacade()
	{
		return aciPaymentFacade;
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
	 * @param aciAccountFacade
	 *           the aciAccountFacade to set
	 */
	public void setAciAccountFacade(final ACIAccountFacade aciAccountFacade)
	{
		this.aciAccountFacade = aciAccountFacade;
	}

	/**
	 * @param aciPaymentFacade
	 *           the aciPaymentFacade to set
	 */
	public void setAciPaymentFacade(final ACIPaymentFacade aciPaymentFacade)
	{
		this.aciPaymentFacade = aciPaymentFacade;
	}
}