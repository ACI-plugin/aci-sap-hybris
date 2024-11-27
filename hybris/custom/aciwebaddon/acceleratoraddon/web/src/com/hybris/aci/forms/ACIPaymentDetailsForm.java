/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;


/**
 */
public class ACIPaymentDetailsForm
{
	private String paymentId;

	private Boolean newBillingAddress;

	private AddressForm billingAddress;
	private boolean useDeliveryAddress;
	private String amount;
	private String billTo_city; // NOSONAR
	private String billTo_country; // NOSONAR
	private String billTo_customerID; // NOSONAR
	private String billTo_email; // NOSONAR
	private String billTo_firstName; // NOSONAR
	private String billTo_lastName; // NOSONAR
	private String billTo_phoneNumber; // NOSONAR
	private String billTo_postalCode; // NOSONAR
	private String billTo_titleCode; // NOSONAR
	private String billTo_state; // NOSONAR
	private String billTo_street1; // NOSONAR
	private String billTo_street2; // NOSONAR
	private String billTo_streetName; // NOSONAR
	private String billTo_streetNumber; // NOSONAR
	private String billTo_houseExtension; // NOSONAR
	private String billTo_phone; // NOSONAR

	private boolean show_form; // NOSONAR

	/**
	 * @return the show_form
	 */
	public boolean isShow_form()
	{
		return show_form;
	}

	/**
	 * @param show_form
	 *           the show_form to set
	 */
	public void setShow_form(final boolean show_form)
	{
		this.show_form = show_form;
	}

	/**
	 * @return the billTo_streetName
	 */
	public String getBillTo_streetName()
	{
		return billTo_streetName;
	}

	/**
	 * @param billTo_streetName
	 *           the billTo_streetName to set
	 */
	public void setBillTo_streetName(final String billTo_streetName)
	{
		this.billTo_streetName = billTo_streetName;
	}

	/**
	 * @return the billTo_streetNumber
	 */
	public String getBillTo_streetNumber()
	{
		return billTo_streetNumber;
	}

	/**
	 * @param billTo_streetNumber
	 *           the billTo_streetNumber to set
	 */
	public void setBillTo_streetNumber(final String billTo_streetNumber)
	{
		this.billTo_streetNumber = billTo_streetNumber;
	}

	/**
	 * @return the billTo_houseExtension
	 */
	public String getBillTo_houseExtension()
	{
		return billTo_houseExtension;
	}

	/**
	 * @param billTo_houseExtension
	 *           the billTo_houseExtension to set
	 */
	public void setBillTo_houseExtension(final String billTo_houseExtension)
	{
		this.billTo_houseExtension = billTo_houseExtension;
	}

	/**
	 * @return the billTo_phone
	 */
	public String getBillTo_phone()
	{
		return billTo_phone;
	}

	/**
	 * @param billTo_phone
	 *           the billTo_phone to set
	 */
	public void setBillTo_phone(final String billTo_phone)
	{
		this.billTo_phone = billTo_phone;
	}

	private String card_accountNumber; // NOSONAR
	private String card_cardType; // NOSONAR
	private String card_startMonth; // NOSONAR
	private String card_startYear; // NOSONAR
	private String card_issueNumber; // NOSONAR
	private String card_cvNumber; // NOSONAR
	private String card_expirationMonth; // NOSONAR
	private String card_expirationYear; // NOSONAR
	private String comments;
	private String currency;
	private String shipTo_city; // NOSONAR
	private String shipTo_country; // NOSONAR
	private String shipTo_firstName; // NOSONAR
	private String shipTo_lastName; // NOSONAR
	private String shipTo_phoneNumber; // NOSONAR
	private String shipTo_postalCode; // NOSONAR
	private String shipTo_shippingMethod; // NOSONAR
	private String shipTo_state; // NOSONAR
	private String shipTo_street1; // NOSONAR
	private String shipTo_street2; // NOSONAR
	private String taxAmount;

	/**
	 * @return the amount
	 */
	public String getAmount()
	{
		return amount;
	}

	/**
	 * @param amount
	 *           the amount to set
	 */
	public void setAmount(final String amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the billTo_city
	 */
	public String getBillTo_city()
	{
		return billTo_city;
	}

	/**
	 * @param billTo_city
	 *           the billTo_city to set
	 */
	public void setBillTo_city(final String billTo_city)
	{
		this.billTo_city = billTo_city;
	}

	/**
	 * @return the billTo_country
	 */
	public String getBillTo_country()
	{
		return billTo_country;
	}

	/**
	 * @param billTo_country
	 *           the billTo_country to set
	 */
	public void setBillTo_country(final String billTo_country)
	{
		this.billTo_country = billTo_country;
	}

	/**
	 * @return the billTo_customerID
	 */
	public String getBillTo_customerID()
	{
		return billTo_customerID;
	}

	/**
	 * @param billTo_customerID
	 *           the billTo_customerID to set
	 */
	public void setBillTo_customerID(final String billTo_customerID)
	{
		this.billTo_customerID = billTo_customerID;
	}

	/**
	 * @return the billTo_email
	 */
	public String getBillTo_email()
	{
		return billTo_email;
	}

	/**
	 * @param billTo_email
	 *           the billTo_email to set
	 */
	public void setBillTo_email(final String billTo_email)
	{
		this.billTo_email = billTo_email;
	}

	/**
	 * @return the billTo_firstName
	 */
	public String getBillTo_firstName()
	{
		return billTo_firstName;
	}

	/**
	 * @param billTo_firstName
	 *           the billTo_firstName to set
	 */
	public void setBillTo_firstName(final String billTo_firstName)
	{
		this.billTo_firstName = billTo_firstName;
	}

	/**
	 * @return the billTo_lastName
	 */
	public String getBillTo_lastName()
	{
		return billTo_lastName;
	}

	/**
	 * @param billTo_lastName
	 *           the billTo_lastName to set
	 */
	public void setBillTo_lastName(final String billTo_lastName)
	{
		this.billTo_lastName = billTo_lastName;
	}

	/**
	 * @return the billTo_phoneNumber
	 */
	public String getBillTo_phoneNumber()
	{
		return billTo_phoneNumber;
	}

	/**
	 * @param billTo_phoneNumber
	 *           the billTo_phoneNumber to set
	 */
	public void setBillTo_phoneNumber(final String billTo_phoneNumber)
	{
		this.billTo_phoneNumber = billTo_phoneNumber;
	}

	/**
	 * @return the billTo_postalCode
	 */
	public String getBillTo_postalCode()
	{
		return billTo_postalCode;
	}

	/**
	 * @param billTo_postalCode
	 *           the billTo_postalCode to set
	 */
	public void setBillTo_postalCode(final String billTo_postalCode)
	{
		this.billTo_postalCode = billTo_postalCode;
	}

	/**
	 * @return the billTo_titleCode
	 */
	public String getBillTo_titleCode()
	{
		return billTo_titleCode;
	}

	/**
	 * @param billTo_titleCode
	 *           the billTo_titleCode to set
	 */
	public void setBillTo_titleCode(final String billTo_titleCode)
	{
		this.billTo_titleCode = billTo_titleCode;
	}

	/**
	 * @return the billTo_state
	 */
	public String getBillTo_state()
	{
		return billTo_state;
	}

	/**
	 * @param billTo_state
	 *           the billTo_state to set
	 */
	public void setBillTo_state(final String billTo_state)
	{
		this.billTo_state = billTo_state;
	}

	/**
	 * @return the billTo_street1
	 */
	public String getBillTo_street1()
	{
		return billTo_street1;
	}

	/**
	 * @param billTo_street1
	 *           the billTo_street1 to set
	 */
	public void setBillTo_street1(final String billTo_street1)
	{
		this.billTo_street1 = billTo_street1;
	}

	/**
	 * @return the billTo_street2
	 */
	public String getBillTo_street2()
	{
		return billTo_street2;
	}

	/**
	 * @param billTo_street2
	 *           the billTo_street2 to set
	 */
	public void setBillTo_street2(final String billTo_street2)
	{
		this.billTo_street2 = billTo_street2;
	}

	/**
	 * @return the card_accountNumber
	 */
	public String getCard_accountNumber()
	{
		return card_accountNumber;
	}

	/**
	 * @param card_accountNumber
	 *           the card_accountNumber to set
	 */
	public void setCard_accountNumber(final String card_accountNumber)
	{
		this.card_accountNumber = card_accountNumber;
	}

	/**
	 * @return the card_cardType
	 */
	public String getCard_cardType()
	{
		return card_cardType;
	}

	/**
	 * @param card_cardType
	 *           the card_cardType to set
	 */
	public void setCard_cardType(final String card_cardType)
	{
		this.card_cardType = card_cardType;
	}

	/**
	 * @return the card_startMonth
	 */
	public String getCard_startMonth()
	{
		return card_startMonth;
	}

	/**
	 * @param card_startMonth
	 *           the card_startMonth to set
	 */
	public void setCard_startMonth(final String card_startMonth)
	{
		this.card_startMonth = card_startMonth;
	}

	/**
	 * @return the card_startYear
	 */
	public String getCard_startYear()
	{
		return card_startYear;
	}

	/**
	 * @param card_startYear
	 *           the card_startYear to set
	 */
	public void setCard_startYear(final String card_startYear)
	{
		this.card_startYear = card_startYear;
	}

	/**
	 * @return the card_issueNumber
	 */
	public String getCard_issueNumber()
	{
		return card_issueNumber;
	}

	/**
	 * @param card_issueNumber
	 *           the card_issueNumber to set
	 */
	public void setCard_issueNumber(final String card_issueNumber)
	{
		this.card_issueNumber = card_issueNumber;
	}

	/**
	 * @return the card_cvNumber
	 */
	public String getCard_cvNumber()
	{
		return card_cvNumber;
	}

	/**
	 * @param card_cvNumber
	 *           the card_cvNumber to set
	 */
	public void setCard_cvNumber(final String card_cvNumber)
	{
		this.card_cvNumber = card_cvNumber;
	}

	/**
	 * @return the card_expirationMonth
	 */
	public String getCard_expirationMonth()
	{
		return card_expirationMonth;
	}

	/**
	 * @param card_expirationMonth
	 *           the card_expirationMonth to set
	 */
	public void setCard_expirationMonth(final String card_expirationMonth)
	{
		this.card_expirationMonth = card_expirationMonth;
	}

	/**
	 * @return the card_expirationYear
	 */
	public String getCard_expirationYear()
	{
		return card_expirationYear;
	}

	/**
	 * @param card_expirationYear
	 *           the card_expirationYear to set
	 */
	public void setCard_expirationYear(final String card_expirationYear)
	{
		this.card_expirationYear = card_expirationYear;
	}

	/**
	 * @return the comments
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * @param comments
	 *           the comments to set
	 */
	public void setComments(final String comments)
	{
		this.comments = comments;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency()
	{
		return currency;
	}

	/**
	 * @param currency
	 *           the currency to set
	 */
	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	/**
	 * @return the shipTo_city
	 */
	public String getShipTo_city()
	{
		return shipTo_city;
	}

	/**
	 * @param shipTo_city
	 *           the shipTo_city to set
	 */
	public void setShipTo_city(final String shipTo_city)
	{
		this.shipTo_city = shipTo_city;
	}

	/**
	 * @return the shipTo_country
	 */
	public String getShipTo_country()
	{
		return shipTo_country;
	}

	/**
	 * @param shipTo_country
	 *           the shipTo_country to set
	 */
	public void setShipTo_country(final String shipTo_country)
	{
		this.shipTo_country = shipTo_country;
	}

	/**
	 * @return the shipTo_firstName
	 */
	public String getShipTo_firstName()
	{
		return shipTo_firstName;
	}

	/**
	 * @param shipTo_firstName
	 *           the shipTo_firstName to set
	 */
	public void setShipTo_firstName(final String shipTo_firstName)
	{
		this.shipTo_firstName = shipTo_firstName;
	}

	/**
	 * @return the shipTo_lastName
	 */
	public String getShipTo_lastName()
	{
		return shipTo_lastName;
	}

	/**
	 * @param shipTo_lastName
	 *           the shipTo_lastName to set
	 */
	public void setShipTo_lastName(final String shipTo_lastName)
	{
		this.shipTo_lastName = shipTo_lastName;
	}

	/**
	 * @return the shipTo_phoneNumber
	 */
	public String getShipTo_phoneNumber()
	{
		return shipTo_phoneNumber;
	}

	/**
	 * @param shipTo_phoneNumber
	 *           the shipTo_phoneNumber to set
	 */
	public void setShipTo_phoneNumber(final String shipTo_phoneNumber)
	{
		this.shipTo_phoneNumber = shipTo_phoneNumber;
	}

	/**
	 * @return the shipTo_postalCode
	 */
	public String getShipTo_postalCode()
	{
		return shipTo_postalCode;
	}

	/**
	 * @param shipTo_postalCode
	 *           the shipTo_postalCode to set
	 */
	public void setShipTo_postalCode(final String shipTo_postalCode)
	{
		this.shipTo_postalCode = shipTo_postalCode;
	}

	/**
	 * @return the shipTo_shippingMethod
	 */
	public String getShipTo_shippingMethod()
	{
		return shipTo_shippingMethod;
	}

	/**
	 * @param shipTo_shippingMethod
	 *           the shipTo_shippingMethod to set
	 */
	public void setShipTo_shippingMethod(final String shipTo_shippingMethod)
	{
		this.shipTo_shippingMethod = shipTo_shippingMethod;
	}

	/**
	 * @return the shipTo_state
	 */
	public String getShipTo_state()
	{
		return shipTo_state;
	}

	/**
	 * @param shipTo_state
	 *           the shipTo_state to set
	 */
	public void setShipTo_state(final String shipTo_state)
	{
		this.shipTo_state = shipTo_state;
	}

	/**
	 * @return the shipTo_street1
	 */
	public String getShipTo_street1()
	{
		return shipTo_street1;
	}

	/**
	 * @param shipTo_street1
	 *           the shipTo_street1 to set
	 */
	public void setShipTo_street1(final String shipTo_street1)
	{
		this.shipTo_street1 = shipTo_street1;
	}

	/**
	 * @return the shipTo_street2
	 */
	public String getShipTo_street2()
	{
		return shipTo_street2;
	}

	/**
	 * @param shipTo_street2
	 *           the shipTo_street2 to set
	 */
	public void setShipTo_street2(final String shipTo_street2)
	{
		this.shipTo_street2 = shipTo_street2;
	}

	/**
	 * @return the taxAmount
	 */
	public String getTaxAmount()
	{
		return taxAmount;
	}

	/**
	 * @param taxAmount
	 *           the taxAmount to set
	 */
	public void setTaxAmount(final String taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	public String getPaymentId()
	{
		return paymentId;
	}

	public void setPaymentId(final String paymentId)
	{
		this.paymentId = paymentId;
	}

	public boolean isUseDeliveryAddress()
	{
		return useDeliveryAddress;
	}

	public void setUseDeliveryAddress(final boolean useDeliveryAddress)
	{
		this.useDeliveryAddress = useDeliveryAddress;
	}

	public Boolean getNewBillingAddress()
	{
		return newBillingAddress;
	}

	public void setNewBillingAddress(final Boolean newBillingAddress)
	{
		this.newBillingAddress = newBillingAddress;
	}

	//	@Valid
	public AddressForm getBillingAddress()
	{
		return billingAddress;
	}

	public void setBillingAddress(final AddressForm billingAddress)
	{
		this.billingAddress = billingAddress;
	}

}
