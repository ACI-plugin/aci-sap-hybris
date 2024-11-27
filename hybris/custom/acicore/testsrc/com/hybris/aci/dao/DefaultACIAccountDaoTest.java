/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.dao;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.Assert;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.hybris.aci.dao.impl.DefaultACIAccountDao;


/**
 * JUnit test suite for {@link DefaultACIAccountDao}
 */
@IntegrationTest
public class DefaultACIAccountDaoTest extends ServicelayerTest
{
	private static final String TEST_CUSTOMER_UID = "accountcustomer@test.com";

	@Resource
	private UserService userService;

	@Resource
	private ACIAccountDao aciAccountDao;

	@Resource
	private ModelService modelService;

	private CustomerModel customer;
	private PaymentInfoModel paymentInfo;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		importCsv("/commerceservices/test/testCustomerAccount.impex", "utf-8");
		customer = userService.getUserForUID(TEST_CUSTOMER_UID, CustomerModel.class);

	}

	@Test
	public void shouldGetAciCardPaymentInfos()
	{
		paymentInfo = modelService.create(PaymentInfoModel.class);
		paymentInfo.setCode("TEST_1");
		paymentInfo.setOwner(customer);
		paymentInfo.setAciCardBin("1234");
		paymentInfo.setAciCardExpiryMonth("12");
		paymentInfo.setAciCardExpiryYear("21");
		paymentInfo.setAciCardNumber("1234");
		paymentInfo.setAciRegistrationId("1234");
		paymentInfo.setUser(customer);
		paymentInfo.setSaved(true);
		paymentInfo.setDuplicate(false);
		modelService.save(paymentInfo);
		final List<PaymentInfoModel> pay = aciAccountDao.getAciCardPaymentInfos(customer);
		Assert.assertCollectionElements(pay, paymentInfo);
	}

	@Test
	public void shouldNotGetAciCardPaymentInfos()
	{
		final List<PaymentInfoModel> pay = aciAccountDao.getAciCardPaymentInfos(customer);
		assertTrue("Fetched collection is expected to be empty", pay.isEmpty());
	}

}
