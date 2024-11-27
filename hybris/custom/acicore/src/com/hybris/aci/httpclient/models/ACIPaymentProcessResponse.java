/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.httpclient.models;

public class ACIPaymentProcessResponse extends BaseResponse
{
	private String paymentType;
	private String registrationId;
	private String paymentBrand;
	private String amount;
	private String currency;
	private String descriptor;
	private Billing billing;
	private Shipping shipping;
	private Card card;
	private Customer customer;
	private String merchantTransactionId;
	private CustomParameters customParameters;
	private String referenceId;

	/**
	 * @return the referenceId
	 */
	public String getReferenceId()
	{
		return referenceId;
	}

	/**
	 * @param referenceId
	 *           the referenceId to set
	 */
	public void setReferenceId(final String referenceId)
	{
		this.referenceId = referenceId;
	}


	/**
	 * @return the paymentType
	 */
	public String getPaymentType()
	{
		return paymentType;
	}

	/**
	 * @param paymentType
	 *           the paymentType to set
	 */
	public void setPaymentType(final String paymentType)
	{
		this.paymentType = paymentType;
	}

	/**
	 * @return the paymentBrand
	 */
	public String getPaymentBrand()
	{
		return paymentBrand;
	}

	/**
	 * @param paymentBrand
	 *           the paymentBrand to set
	 */
	public void setPaymentBrand(final String paymentBrand)
	{
		this.paymentBrand = paymentBrand;
	}

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
	 * @return the descriptor
	 */
	public String getDescriptor()
	{
		return descriptor;
	}

	/**
	 * @param descriptor
	 *           the descriptor to set
	 */
	public void setDescriptor(final String descriptor)
	{
		this.descriptor = descriptor;
	}

	/**
	 * @return the billing
	 */
	public Billing getBilling()
	{
		return billing;
	}

	/**
	 * @param billing
	 *           the billing to set
	 */
	public void setBilling(final Billing billing)
	{
		this.billing = billing;
	}

	/**
	 * @return the shipping
	 */
	public Shipping getShipping()
	{
		return shipping;
	}

	/**
	 * @param shipping
	 *           the shipping to set
	 */
	public void setShipping(final Shipping shipping)
	{
		this.shipping = shipping;
	}

	/**
	 * @return the registrationId
	 */
	public String getRegistrationId()
	{
		return registrationId;
	}

	/**
	 * @param registrationId
	 *           the registrationId to set
	 */
	public void setRegistrationId(final String registrationId)
	{
		this.registrationId = registrationId;
	}

	/**
	 * @return the card
	 */
	public Card getCard()
	{
		return card;
	}

	/**
	 * @param card
	 *           the card to set
	 */
	public void setCard(final Card card)
	{
		this.card = card;
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer()
	{
		return customer;
	}

	/**
	 * @param customer
	 *           the customer to set
	 */
	public void setCustomer(final Customer customer)
	{
		this.customer = customer;
	}

	/**
	 * @return the merchantTransactionId
	 */
	public String getMerchantTransactionId()
	{
		return merchantTransactionId;
	}

	/**
	 * @param merchantTransactionId
	 *           the merchantTransactionId to set
	 */
	public void setMerchantTransactionId(final String merchantTransactionId)
	{
		this.merchantTransactionId = merchantTransactionId;
	}

	/**
	 * @return the customParameters
	 */
	public CustomParameters getCustomParameters()
	{
		return customParameters;
	}

	/**
	 * @param customParameters
	 *           the customParameters to set
	 */
	public void setCustomParameters(final CustomParameters customParameters)
	{
		this.customParameters = customParameters;
	}

}
