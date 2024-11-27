/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class ACIUtilConstants
{
	public static final String ACI_BASEURL = "aci.baseurl";
	public static final String ACI_TEST_BASEURL = "aci.test.baseurl";
	public static final String ACI_VERSION = "aci.version";
	public static final String ACI_MODE = "aci.mode";
	public static final String ACI_TEST_MODE = "aci.testMode";
	public static final String ACI_FORCE_RESULT_CODE = "aci.forceResultCode";
	public static final String ACI_SCRIPT_PATH = "aci.scriptpath";

	public static final String AMOUNT = " AMOUNT  - ";
	public static final String CURRENCY = " CURRENCY - ";

	public static final String ACI_LIVE = "LIVE";
	public static final String ACI_TEST = "TEST";

	public static final String SLASH = "/";

	public static final String PAYMENT_CONFIRMED = "CONFIRMED";
	public static final String PAYMENT_REVIEW = "MANUAL_REVIEW";
	public static final String PAYMENT_PENDING = "PENDING";
	public static final String PAYMENT_REJECTED = "REJECTED";

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";

	public static final String CHECKOUTS = "checkouts";

	public static final String PAYMENT = "payment";

	public static final String SHOPPER_RESULT_URL = "shopper_result_url";
	public static final String REGISTRATION = "registration";
	public static final String ACI_SCRIPT_URL = "aci_script_url";

	public static final String GUEST = "GUEST";
	public static final String NEW = "NEW";
	public static final String REGISTERED = "REGISTERED";
	public static final String EXISTING = "EXISTING";
	public static final String SALE = "SALE";
	public static final String AUTHORIZE = "AUTHORIZE";
	public static final String CAPTURE = "CAPTURE";
	public static final String REFUND = "REFUND";
	public static final String REVERSAL = "REVERSAL";

	public static final String PREAUTHORIZATION_CODE = "PA";
	public static final String DEBIT_CODE = "DB";
	public static final String CREDIT_CODE = "CD";
	public static final String CAPTURE_CODE = "CP";
	public static final String REVERSAL_CODE = "RV";
	public static final String REFUND_CODE = "RF";

	public static final String MASK = "************";

	public static final String SESSION_INVALID = "SESSION_INVALID";
	public static final String ORDER_MISMATCH = "ORDER_MISMATCH";
	public static final String ACI_SERVICE_ERROR = "ACI_SERVICE_ERROR";
	public static final String TRANSACTION_REJECTED = "TRANSACTION_REJECTED";
	public static final String TRANSACTIONID_MISSING = "TRANSACTIONID_MISSING";
	public static final String CANCELLED_BY_CUSTOMER = "CANCELLED_BY_CUSTOMER";
	public static final String CODE_CUSTOMER_CANEL = "100.396.101";
	public static final String ORDER_TAMPERED = "ORDER_TAMPERED";


	public static final String ACI_AUTH_ACCEPTED = "ACI_AUTH_ACCEPTED";
	public static final String ACI_AUTH_DECLINED = "ACI_AUTH_DECLINED";
	public static final String ACI_AUTH_PENDING = "ACI_AUTH_PENDING";

	public static final String CURRENT_MONTH = "current_month";
	public static final String CURRENT_YEAR = "current_year";

	public static List<String> prepareCheckoutStatuses = new ArrayList<String>();

	public static final Map<String, String> aciCustomerStatusMap = new HashMap<String, String>();
	static
	{
		aciCustomerStatusMap.put(GUEST, NEW);
		aciCustomerStatusMap.put(REGISTERED, EXISTING);

	}
	public static Map<String, String> aciPaymentTypeMap = new HashMap<String, String>();
	public static final String ADD_OR_EDIT = "addOrEdit";
	static
	{
		aciPaymentTypeMap.put(SALE, DEBIT_CODE);
		aciPaymentTypeMap.put(AUTHORIZE, PREAUTHORIZATION_CODE);
		aciPaymentTypeMap.put(CAPTURE, CAPTURE_CODE);
		aciPaymentTypeMap.put(REFUND, REFUND_CODE);
		aciPaymentTypeMap.put(REVERSAL, REVERSAL_CODE);

	}
	static
	{
		prepareCheckoutStatuses.add("000.200.100");
		prepareCheckoutStatuses.add("000.200.101");
	}
	public static final Map<String, String> aciPaymentStatusMap = new HashMap<String, String>();

	static
	{
		aciPaymentStatusMap.put(PAYMENT_CONFIRMED, ACI_AUTH_ACCEPTED);
		aciPaymentStatusMap.put(PAYMENT_PENDING, ACI_AUTH_PENDING);
		aciPaymentStatusMap.put(PAYMENT_REVIEW, ACI_AUTH_PENDING);
		aciPaymentStatusMap.put(PAYMENT_REJECTED, ACI_AUTH_DECLINED);
	}

	public static final Map<String, String> threeDSChallengeIndicatorMap = new HashMap<String, String>();

	static
	{
		threeDSChallengeIndicatorMap.put("NO_PREFERENCE", "01");
		threeDSChallengeIndicatorMap.put("NO_CHALLENGE_REQUESTED", "02");
		threeDSChallengeIndicatorMap.put("CHALLENGE_REQUESTED_3D_SECURE_REQUESTOR_PREFERENCE", "03");
		threeDSChallengeIndicatorMap.put("CHALLENGE_REQUESTED_MANDATE", "04");
	}

	public static final Map<String, String> threeDSAuthenticationMap = new HashMap<String, String>();

	static
	{
		threeDSAuthenticationMap.put("STATIC", "01");
		threeDSAuthenticationMap.put("DYNAMIC", "02");
		threeDSAuthenticationMap.put("OOB", "03");
		threeDSAuthenticationMap.put("DECOUPLED", "04");
	}

	public static final Map<String, String> threeDSExemptionFlagMap = new HashMap<String, String>();

	static
	{
		threeDSExemptionFlagMap.put("LOW_VALUE_EXEMPTION", "01");
		threeDSExemptionFlagMap.put("TRA_EXEMPTION", "02");
		threeDSExemptionFlagMap.put("TRUSTED_BENEFICIARY_EXEMPTION", "03");
		threeDSExemptionFlagMap.put("CORPORATE_CARD_PAYMENT_EXEMPTION", "04");
	}

	private ACIUtilConstants()
	{
		throw new IllegalStateException("Utility class");
	}
}
