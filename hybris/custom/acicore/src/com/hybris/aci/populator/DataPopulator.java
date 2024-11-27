/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.populator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.aci.payment.data.ACIConfigData;
import com.hybris.aci.constants.ACIUtilConstants;
import com.hybris.aci.constants.AuthenticationConstants;
import com.hybris.aci.constants.BasicPaymentConstants;
import com.hybris.aci.constants.BillingAddressConstants;
import com.hybris.aci.constants.CardConstants;
import com.hybris.aci.constants.CartConstants;
import com.hybris.aci.constants.CustomConstants;
import com.hybris.aci.constants.CustomerConstants;
import com.hybris.aci.constants.MerchantConstants;
import com.hybris.aci.constants.ShippingAddressConstants;
import com.hybris.aci.constants.ThreeDSecureConstants;


public class DataPopulator
{

	public void populateAuthData(final Map<String, String> m, final String entityId)
	{
		m.put(AuthenticationConstants.AUTHENTICATION_ENTITYID, entityId);

	}

	/**
	 * Create the base data for Backend
	 *
	 * @param amount
	 * @param currency
	 * @param m
	 * @param paymentOption
	 */
	public void populateBaseDataForBackEnd(final String amount, final String currency, final Map<String, String> m,
			final String paymentOption)
	{
		final String paymentType = ACIUtilConstants.aciPaymentTypeMap.get(paymentOption);
		if (paymentType != ACIUtilConstants.REVERSAL_CODE)
		{
			m.put(BasicPaymentConstants.AMOUNT, amount);
			m.put(BasicPaymentConstants.CURRENCY, currency);
		}
		m.put(BasicPaymentConstants.PAYMENTTYPE, paymentType);

	}

	/**
	 * Creates base order data
	 *
	 * @param order
	 * @param amount
	 * @param m
	 * @param createRegistration
	 * @param paymentOption
	 */
	public void populateBaseData(final AbstractOrderModel order, final BigDecimal amount, final Map<String, String> m,
			final boolean createRegistration, final String paymentOption)
	{

		final String paymentType = ACIUtilConstants.aciPaymentTypeMap.get(paymentOption);
		m.put(BasicPaymentConstants.AMOUNT, String.format("%.2f", amount));
		m.put(BasicPaymentConstants.CURRENCY, order.getCurrency().getIsocode());

		if (createRegistration)
		{
			m.put(BasicPaymentConstants.CREATEREGISTRATION, "true");

		}
		else
		{
			m.put(BasicPaymentConstants.PAYMENTTYPE, paymentType);
		}

		m.put(BasicPaymentConstants.TRANSACTIONCATEGORY, "EC");
		m.put(BasicPaymentConstants.MERCHANTTRANSACTIONID, "ACI" + order.getCode());
	}

	/**
	 * Populate the billing address
	 *
	 * @param cartModel
	 * @param m
	 */
	public void populateBillingAddress(final AbstractOrderModel cartModel, final Map<String, String> m)
	{
		if (cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null)
		{
			final AddressModel addressData = cartModel.getPaymentInfo().getBillingAddress();

			m.put(BillingAddressConstants.BILLING_STREET1, addressData.getStreetname());
			m.put(BillingAddressConstants.BILLING_STREET2, addressData.getStreetnumber());
			m.put(BillingAddressConstants.BILLING_CITY, addressData.getTown());
			if (addressData.getRegion() != null)
			{
				m.put(BillingAddressConstants.BILLING_STATE, addressData.getRegion().getIsocode());
			}
			//m.put(BillingAddressConstants.BILLING_HOUSENUMBER1, addressData.get);
			//m.put(BillingAddressConstants.BILLING_HOUSENUMBER2, addressData.get);
			m.put(BillingAddressConstants.BILLING_COUNTRY, addressData.getCountry().getIsocode());
			m.put(BillingAddressConstants.BILLING_POSTCODE, addressData.getPostalcode());
		}

	}

	/**
	 * Populate shipping address
	 *
	 * @param order
	 * @param m
	 * @param email
	 */
	public void populateShippingAddress(final AbstractOrderModel order, final Map<String, String> m, final String email)
	{
		if (order.getDeliveryAddress() != null)
		{
			final AddressModel address = order.getDeliveryAddress();
			m.put(ShippingAddressConstants.SHIPPING_STREET1, address.getStreetname());
			m.put(ShippingAddressConstants.SHIPPING_STREET2, address.getStreetnumber());
			m.put(ShippingAddressConstants.SHIPPING_CITY, address.getTown());
			if (address.getRegion() != null)
			{
				m.put(ShippingAddressConstants.SHIPPING_STATE, address.getRegion().getIsocode());
			}
			m.put(ShippingAddressConstants.SHIPPING_COUNTRY, address.getCountry().getIsocode());
			m.put(ShippingAddressConstants.SHIPPING_POSTCODE, address.getPostalcode());
			m.put(ShippingAddressConstants.SHIPPING_CUSTOMER_GIVENNAME, address.getFirstname());
			m.put(ShippingAddressConstants.SHIPPING_CUSTOMER_SURNAME, address.getLastname());
			m.put(ShippingAddressConstants.SHIPPING_CUSTOMER_EMAIL, address.getEmail() != null ? address.getEmail() : email);
			m.put(ShippingAddressConstants.SHIPPING_CUSTOMER_PHONE, address.getPhone1());

		}
		if (order.getDeliveryMode() != null)
		{
			m.put(ShippingAddressConstants.SHIPPING_METHOD, order.getDeliveryMode().getAciShippingMode().getCode());
		}
	}

	/**
	 * Populate customer data
	 *
	 * @param order
	 * @param m
	 */
	public void populateCustomer(final AbstractOrderModel order, final Map<String, String> m, final CustomerModel customer)
	{
		if (order.getDeliveryAddress() != null)
		{
			final AddressModel address = order.getDeliveryAddress();
			m.put(CustomerConstants.CUSTOMER_MERCHANTCUSTOMERID, customer.getCustomerID());
			m.put(CustomerConstants.CUSTOMER_GIVENNAME, address.getFirstname());
			m.put(CustomerConstants.CUSTOMER_SURNAME, address.getLastname());
			m.put(CustomerConstants.CUSTOMER_MOBILE, address.getPhone1());
		}

	}

	/**
	 * populate customer status
	 *
	 * @param customer
	 * @param m
	 * @param ipAddress
	 * @param email
	 */
	public void populateCustomerDetails(final CustomerModel customer, final Map<String, String> m, final String ipAddress,
			final String email)
	{
		if (customer.getType() == null)
		{
			m.put(CustomerConstants.CUSTOMER_STATUS, ACIUtilConstants.aciCustomerStatusMap.get(ACIUtilConstants.REGISTERED));
		}
		else
		{
			m.put(CustomerConstants.CUSTOMER_STATUS, ACIUtilConstants.aciCustomerStatusMap.get(customer.getType().getCode()));
		}
		m.put(CustomerConstants.CUSTOMER_IP, ipAddress);
		m.put(CustomerConstants.CUSTOMER_EMAIL, email);

	}

	/**
	 * populate data for aci card registration
	 *
	 * @param customerData
	 * @param m
	 * @param ipAddress
	 */
	public void populateCustomerForRegistration(final CustomerData customerData, final Map<String, String> m,
			final String ipAddress)
	{
		if (customerData != null)
		{
			m.put(CustomerConstants.CUSTOMER_GIVENNAME, customerData.getFirstName());
			m.put(CustomerConstants.CUSTOMER_SURNAME, customerData.getLastName());
			m.put(CustomerConstants.CUSTOMER_STATUS, ACIUtilConstants.aciCustomerStatusMap.get("REGISTERED"));
			m.put(CustomerConstants.CUSTOMER_IP, ipAddress);
			m.put(CustomerConstants.CUSTOMER_EMAIL, customerData.getUid());
		}
		m.put(BasicPaymentConstants.CREATEREGISTRATION, "true");
	}

	/**
	 * Populate cart data
	 *
	 * @param order
	 * @param m
	 */
	public void populateCartData(final AbstractOrderModel order, final Map<String, String> m)
	{
		int i = 0;
		final Double taxRate = getTaxRate(order);
		for (final AbstractOrderEntryModel orderitem : order.getEntries())
		{
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.CURRENCY, order.getCurrency().getIsocode());
			Double totalItemPrice;
			if (order.getNet().booleanValue())
			{
				totalItemPrice = Double
						.valueOf(orderitem.getTotalPrice().doubleValue() + TaxValue.sumAppliedTaxValues(orderitem.getTaxValues()));
			}
			else
			{
				totalItemPrice = orderitem.getTotalPrice();
			}
			double discounts = 0.0d;
			final List<DiscountValue> discountValues = orderitem.getDiscountValues();
			if (discountValues != null && !discountValues.isEmpty())
			{
				for (final DiscountValue dValue : discountValues)
				{
					discounts += dValue.getAppliedValue();
				}
			}
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.DISCOUNT, String.format("%.2f", new Double(discounts)));

			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.ORIGINALPRICE,
					String.format("%.2f", orderitem.getBasePrice()));
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.PRICE, String.format("%.2f",
					new Double((totalItemPrice.doubleValue() - discounts) / orderitem.getQuantity().doubleValue())));


			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.QUANTITY, orderitem.getQuantity().toString());
			if (orderitem.getDeliveryMode() != null)
			{
				m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.SHIPPING,
						orderitem.getDeliveryMode().getAciShippingMode().getCode());
			}
			//m.put(CartConstants.CART_ITEMS + "[" + i + "]." +CartConstants.SHIPPINGINSTRUCTIONS, "");
			//m.put(CartConstants.CART_ITEMS + "[" + i + "]." +CartConstants.SHIPPINGMETHOD, "");
			//m.put(CartConstants.CART_ITEMS + "[" + i + "]." +CartConstants.SHIPPINGTRACKINGNUMBER, "");

			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.NAME, orderitem.getProduct().getName());
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.SKU, orderitem.getProduct().getCode());
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TAX, String.format("%.2f", taxRate));

			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALTAXAMOUNT,
					String.format("%.2f", new Double(TaxValue.sumAppliedTaxValues(orderitem.getTaxValues()))));
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALAMOUNT, String.format("%.2f", totalItemPrice));


			i = i + 1;
		}
		// Add shipping details

		addShippingDetails(i, m, order, taxRate);
		i = i + 1;
		// Add discount details

		addDiscountDetails(i, m, order, taxRate);
	}

	private void addShippingDetails(final int i, final Map<String, String> m, final AbstractOrderModel order, final Double taxRate)
	{
		final Double deliveryCost = order.getDeliveryCost();

		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.NAME, "shipping_fee");
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.QUANTITY, "1");
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.CURRENCY, order.getCurrency().getIsocode());
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.DESCRIPTION, "Shipping Cost");
		double totalDelivery = deliveryCost.doubleValue();
		if (order.getNet().booleanValue())
		{

			final Double deliveryCostTax = calculateDeliveryTaxAmount(deliveryCost, taxRate);
			totalDelivery = deliveryCost.doubleValue() + deliveryCostTax.doubleValue();
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TAX, String.format("%.2f", taxRate));
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALTAXAMOUNT, String.format("%.2f", deliveryCostTax));
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALAMOUNT,
					String.format("%.2f", Double.valueOf(totalDelivery)));

		}
		else
		{
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TAX, "0");
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALTAXAMOUNT, "0");
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALAMOUNT,
					String.format("%.2f", Double.valueOf(totalDelivery)));

		}
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.PRICE,
				String.format("%.2f", Double.valueOf(totalDelivery)));

	}

	private void addDiscountDetails(final int i, final Map<String, String> m, final AbstractOrderModel order, final Double taxRate)
	{
		final Double discountValue = getGlobalDiscountValue(order);
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.NAME, "Global Discount");
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.QUANTITY, "1");
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.PRICE, String.format("%.2f", discountValue));
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.CURRENCY, order.getCurrency().getIsocode());
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.DESCRIPTION, "Order Discount");
		double totalDiscount = discountValue.doubleValue();
		if (order.getNet().booleanValue())
		{
			final Double discountTax = calculateGlobalTotalTaxAmount(discountValue, taxRate);
			totalDiscount = discountValue.doubleValue() + discountTax.doubleValue();
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TAX, String.format("%.2f", taxRate));
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALTAXAMOUNT, String.format("%.2f", discountTax));
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALAMOUNT,
					String.format("%.2f", Double.valueOf(totalDiscount)));
		}
		else
		{
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TAX, "0");
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALTAXAMOUNT, "0");
			m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.TOTALAMOUNT,
					String.format("%.2f", Double.valueOf(totalDiscount)));
		}
		m.put(CartConstants.CART_ITEMS + "[" + i + "]." + CartConstants.PRICE,
				String.format("%.2f", Double.valueOf(totalDiscount)));
	}

	private Double getTaxRate(final AbstractOrderModel source)
	{
		Double taxRate = new Double(0.0D);
		final Collection<TaxValue> allTaxValues = source.getTotalTaxValues();
		if (CollectionUtils.isNotEmpty(allTaxValues))
		{
			for (final TaxValue tx : allTaxValues)
			{
				if (!tx.isAbsolute())
				{
					taxRate = new Double(tx.getValue());
				}
			}

		}
		return taxRate;
	}

	protected Double getGlobalDiscountValue(final AbstractOrderModel source)
	{
		final List<DiscountValue> discounts = source.getGlobalDiscountValues();
		double discountValue = 0.0;
		for (final DiscountValue discount : discounts)
		{
			discountValue += discount.getAppliedValue();
		}
		discountValue = -discountValue;

		return Double.valueOf(discountValue);
	}

	private Double calculateGlobalTotalTaxAmount(final double totalDiscount, final double taxRate)
	{
		final double discount = (totalDiscount * 100 * taxRate) / 10000;
		return (Double.valueOf(discount));
	}

	private Double calculateDeliveryTaxAmount(final double deliveryCost, final double taxRate)
	{
		final double deliveryTaxAmount = deliveryCost * 100 * taxRate / 10000;
		return (Double.valueOf(deliveryTaxAmount));
	}

	public void populateRegistrationIds(final Collection<PaymentInfoModel> paymentInfos, final String allowedCards,
			final Map<String, String> m)
	{
		final String[] l = allowedCards.split(" ");
		if (l.length > 0)
		{
			final List<String> cardList = Arrays.asList(l);
			int i = 0;

			for (final PaymentInfoModel paymentInfo : paymentInfos)
			{
				if (StringUtils.isNotEmpty(paymentInfo.getAciRegistrationId()) && cardList.contains(paymentInfo.getAciPaymentBrand()))
				{
					m.put(CartConstants.REGISTRATIONS + "[" + i + "].id", paymentInfo.getAciRegistrationId());
					i++;

				}
			}
		}
	}

	public void populateRegistrationStandalone(final CCPaymentInfoData paymentInfoData, final String cvNumber, final String brand,
			final String entityId, final Map<String, String> m)
	{
		m.put(AuthenticationConstants.AUTHENTICATION_ENTITYID, entityId);
		m.put(BasicPaymentConstants.PAYMENTBRAND, brand);
		m.put(CardConstants.CARD_HOLDER, paymentInfoData.getAccountHolderName());
		m.put(CardConstants.CARD_NUMBER, paymentInfoData.getCardNumber());
		m.put(CardConstants.CARD_EXPIRYMONTH, paymentInfoData.getExpiryMonth());
		m.put(CardConstants.CARD_EXPIRYYEAR, paymentInfoData.getExpiryYear());
		m.put(CardConstants.CARD_CVV, cvNumber);
	}

	/**
	 * populate custom data. Merchants can customize this
	 *
	 * @param testMode
	 * @param forceResultCode
	 * @param m
	 */
	public void populateCustomData(final String testMode, final String forceResultCode, final ACIConfigData aciConfig,
			final Map<String, String> m)
	{
		if (StringUtils.isNotEmpty(testMode))
		{
			m.put(CustomConstants.CUSTOM_TESTMODE, testMode);
		}
		if (StringUtils.isNotEmpty(forceResultCode))
		{
			m.put(CustomConstants.CUSTOM + "[" + CustomConstants.CUSTOM_FORCERESULTCODE + "]", forceResultCode);
		}
		final String hybrisVersion = Config.getParameter(CustomConstants.HYBRISVERSION);
		if (StringUtils.isNotEmpty(hybrisVersion))
		{
			m.put(CustomConstants.CUSTOM + "[" + CustomConstants.HYBRISVERSION + "]",
					Config.getParameter(CustomConstants.HYBRISVERSION));
		}
		m.put(CustomConstants.CUSTOM + "[UAT_TEST]", "true");
		if (aciConfig.getIsThreeDSEnabled().booleanValue())
		{
			populate3DSecureData(aciConfig, m);
		}
		//TODO
		/*********** Customization can be done here to send additional Custom Data. **************/

	}

	/**
	 * populate custom data. Merchants can customize this
	 *
	 * @param testMode
	 * @param forceResultCode
	 * @param m
	 */
	public void populate3DSecureData(final ACIConfigData aciConfig, final Map<String, String> m)
	{
		if (StringUtils.isNotEmpty(aciConfig.getThreeDSChallengeIndicator()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "." + ThreeDSecureConstants.CHALLENGEINDICATOR
					+ "]", aciConfig.getThreeDSChallengeIndicator());
		}
		if (StringUtils.isNotEmpty(aciConfig.getThreeDSAuthentication()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "." + ThreeDSecureConstants.AUTHENTICATIONTYPE
					+ "]", "2");
		}
		if (StringUtils.isNotEmpty(aciConfig.getThreeDSExemptionFlag()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "." + ThreeDSecureConstants.EXEMPTIONFLAG
					+ "]", aciConfig.getThreeDSExemptionFlag());
		}
		if (StringUtils.isNotEmpty(aciConfig.getThreeDSVersion()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "." + ThreeDSecureConstants.VERSION + "]",
					aciConfig.getThreeDSVersion());
		}
		if (StringUtils.isNotEmpty(aciConfig.getThreeDSchallengeMandatedInd()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "."
					+ ThreeDSecureConstants.CHALLENGEMANDATEDINDICATOR + "]", aciConfig.getThreeDSchallengeMandatedInd());
		}
		if (StringUtils.isNotEmpty(aciConfig.getTransactionStatusReason()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "."
					+ ThreeDSecureConstants.TRANSACTIONSTATUSREASON + "]", aciConfig.getTransactionStatusReason());
		}
		if (StringUtils.isNotEmpty(aciConfig.getAcsTransactionId()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "." + ThreeDSecureConstants.ACSTRANSACTIONID
					+ "]", aciConfig.getAcsTransactionId());
		}
		if (StringUtils.isNotEmpty(aciConfig.getDsTransactionId()))
		{
			m.put(CustomConstants.CUSTOM + "[" + ThreeDSecureConstants.THREEDSECURE + "." + ThreeDSecureConstants.DSTRANSACTIONID
					+ "]", aciConfig.getDsTransactionId());
		}
		//TODO
		/*********** Customization can be done here to send additional Custom Data. **************/

	}

	/**
	 * populate risk data. Merchants can customize this
	 *
	 * @param test
	 * @param m
	 */
	public void populateRiskData(final String test, final Map<String, String> m)
	{
		//TODO
		/*********** Customization can be done here to send risk Data. **************/
		/*********** Just a sample data is provided here *****************************/
		if (StringUtils.isNotEmpty(test))
		{
			//m.put(RiskConstants.RISK + "[" + RiskConstants.CUSTOM_TEST + "]", test);
		}
		//m.put(RiskConstants.RISK + "[UAT_TEST]", "true");
	}

	/**
	 * Populates merchants data. Merchants can customize this
	 *
	 * @param m
	 */
	public void populateMerchantData(final Map<String, String> m)
	{
		/*********** Customization can be done here to send risk Data. **************/
		/*********** Just a sample data is provided here *****************************/

		m.put(MerchantConstants.MERCHANT_CITY, Config.getParameter(MerchantConstants.MERCHANT_CITY));
		m.put(MerchantConstants.MERCHANT_COUNTRY, Config.getParameter(MerchantConstants.MERCHANT_COUNTRY));
		m.put(MerchantConstants.MERCHANT_MCC, Config.getParameter(MerchantConstants.MERCHANT_MCC));
		m.put(MerchantConstants.MERCHANT_NAME, Config.getParameter(MerchantConstants.MERCHANT_NAME));
		m.put(MerchantConstants.MERCHANT_PHONE, Config.getParameter(MerchantConstants.MERCHANT_PHONE));
		m.put(MerchantConstants.MERCHANT_POSTCODE, Config.getParameter(MerchantConstants.MERCHANT_POSTCODE));
		m.put(MerchantConstants.MERCHANT_STATE, Config.getParameter(MerchantConstants.MERCHANT_STATE));
		m.put(MerchantConstants.MERCHANT_STREET, Config.getParameter(MerchantConstants.MERCHANT_STREET));
		m.put(MerchantConstants.MERCHANT_SUBMERCHANTID, Config.getParameter(MerchantConstants.MERCHANT_SUBMERCHANTID));
	}
}
