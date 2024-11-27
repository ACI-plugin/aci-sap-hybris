/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.aci.model.ACIConfigModel;
import com.hybris.aci.httpclient.ACIPayment;
import com.hybris.aci.httpclient.CheckoutOrder;
import com.hybris.aci.httpclient.models.ACIPaymentProcessResponse;
import com.hybris.aci.httpclient.models.Result;
import com.hybris.aci.service.ACIConfigService;
import com.hybris.aci.service.ACITransactionService;


/**
 *
 */
@UnitTest
public class DefaultACIPaymentServiceImplTest
{

	private DefaultACIPaymentService defaultACIPaymentService = new DefaultACIPaymentService();

	@Mock
	private ACIConfigService aciConfigService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ModelService modelService;
	@Mock
	private ACITransactionService aciTransactionService;
	@Mock
	private ACIConfigModel aciConfigModel;

	ACIPaymentProcessResponse aciResponse;
	@Mock
	private CheckoutOrder checkoutOrder;

	@Mock
	private ACIPayment aciPayment;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		//Registry.activateMasterTenant();
		defaultACIPaymentService = Mockito.spy(new DefaultACIPaymentService());
		Mockito.doReturn(aciConfigService).when(defaultACIPaymentService).getAciConfigService();
		Mockito.doReturn(commonI18NService).when(defaultACIPaymentService).getCommonI18NService();
		Mockito.doReturn(modelService).when(defaultACIPaymentService).getModelService();
		Mockito.doReturn(aciTransactionService).when(defaultACIPaymentService).getAciTransactionService();
		Mockito.doReturn(aciConfigModel).when(aciConfigService).getACIConfig();
		Mockito.when(aciConfigModel.getEntityId()).thenReturn("TEST_ENTITY_ID");
		Mockito.when(aciConfigModel.getBearerToken()).thenReturn("TOKEN");
		Mockito.when(aciConfigService.getBaseUrl()).thenReturn("https://test.oppwa.com");
		Mockito.when(aciConfigService.getAPiVersionFromConfig()).thenReturn("v1");

		System.out.print("DFGGG");
	}

	@Test
	public void testCapture()
	{
		aciResponse = new ACIPaymentProcessResponse();
		aciResponse.setOk(true);
		aciResponse.setResult(new Result());
		aciResponse.setMerchantTransactionId("1");
		final OrderModel order = mock(OrderModel.class);
		final PaymentTransactionModel transaction = mock(PaymentTransactionModel.class);
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);
		final PaymentTransactionEntryModel pte = mock(PaymentTransactionEntryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		//final DataPopulator dp = mock(DataPopulator.class);
		final PaymentTransactionEntryModel captureEntry = mock(PaymentTransactionEntryModel.class);
		Mockito.when(currency.getIsocode()).thenReturn("GBP");
		final Iterator<PaymentTransactionModel> iterator = mock(Iterator.class);
		final List<PaymentTransactionModel> list = mock(List.class);

		when(list.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn(transaction);

		Mockito.doReturn(list).when(order).getPaymentTransactions();

		final List<PaymentTransactionEntryModel> mockList = Mockito.mock(List.class);
		Mockito.doReturn(mockList).when(transaction).getEntries();

		final Iterator<PaymentTransactionEntryModel> mockIterator = Mockito.mock(Iterator.class);

		when(mockList.iterator()).thenReturn(mockIterator);
		when(mockIterator.hasNext()).thenReturn(true, false);
		Mockito.doReturn(pte).when(mockIterator).next();
		Mockito.doReturn(currency).when(pte).getCurrency();
		Mockito.doReturn(currency).when(order).getCurrency();
		Mockito.when(pte.getAmount()).thenReturn(new BigDecimal(100.00));
		Mockito.when(pte.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
		Mockito.doReturn(baseStore).when(order).getStore();
		Mockito.doReturn(aciConfigModel).when(baseStore).getACIConfig();

		Mockito.doNothing().when(aciTransactionService).saveTransactionResponse(aciResponse, order);
		Mockito.doReturn(captureEntry).when(aciTransactionService).createPaymentTransactionEntryModel(
				Mockito.any(PaymentTransactionModel.class), Mockito.any(ACIPaymentProcessResponse.class));
		Mockito.doReturn("1").when(order).getAciMerchantTransactionId();
		Mockito.doReturn(aciResponse).when(defaultACIPaymentService).processPaymentActions(Mockito.any(OrderModel.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(aciResponse).when(aciPayment).doPaymentAction(Matchers.anyMap(), Matchers.anyString(),
				Matchers.anyString());

		Mockito.doNothing().when(modelService).saveAll();
		defaultACIPaymentService.capture(order);
		assertEquals(true, aciResponse.isOk());
	}

	@Test
	public void testPartialCapture()
	{
		aciResponse = new ACIPaymentProcessResponse();
		aciResponse.setOk(true);
		aciResponse.setResult(new Result());
		aciResponse.setMerchantTransactionId("1");
		final OrderModel order = mock(OrderModel.class);
		final PaymentTransactionModel transaction = mock(PaymentTransactionModel.class);
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);
		final PaymentTransactionEntryModel pte = mock(PaymentTransactionEntryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		//final DataPopulator dp = mock(DataPopulator.class);
		final PaymentTransactionEntryModel captureEntry = mock(PaymentTransactionEntryModel.class);
		Mockito.when(currency.getIsocode()).thenReturn("GBP");
		final Iterator<PaymentTransactionModel> iterator = mock(Iterator.class);
		final List<PaymentTransactionModel> list = mock(List.class);

		when(list.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn(transaction);

		Mockito.doReturn(list).when(order).getPaymentTransactions();

		final List<PaymentTransactionEntryModel> mockList = Mockito.mock(List.class);
		Mockito.doReturn(mockList).when(transaction).getEntries();

		final Iterator<PaymentTransactionEntryModel> mockIterator = Mockito.mock(Iterator.class);

		when(mockList.iterator()).thenReturn(mockIterator);
		when(mockIterator.hasNext()).thenReturn(true, false);
		Mockito.doReturn(pte).when(mockIterator).next();
		Mockito.doReturn(currency).when(pte).getCurrency();
		Mockito.doReturn(currency).when(order).getCurrency();
		Mockito.when(pte.getAmount()).thenReturn(new BigDecimal(100.00));
		Mockito.when(pte.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
		Mockito.doReturn(baseStore).when(order).getStore();
		Mockito.doReturn(aciConfigModel).when(baseStore).getACIConfig();

		Mockito.doNothing().when(aciTransactionService).saveTransactionResponse(aciResponse, order);
		Mockito.doReturn(captureEntry).when(aciTransactionService).createPaymentTransactionEntryModel(
				Mockito.any(PaymentTransactionModel.class), Mockito.any(ACIPaymentProcessResponse.class));
		Mockito.doReturn("1").when(order).getAciMerchantTransactionId();
		Mockito.doReturn(aciResponse).when(defaultACIPaymentService).processPaymentActions(Mockito.any(OrderModel.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(aciResponse).when(aciPayment).doPaymentAction(Matchers.anyMap(), Matchers.anyString(),
				Matchers.anyString());

		Mockito.doNothing().when(modelService).saveAll();
		defaultACIPaymentService.partialCapture(order, "20");
		assertEquals(true, aciResponse.isOk());
	}

	@Test
	public void testRefund()
	{
		aciResponse = new ACIPaymentProcessResponse();
		aciResponse.setOk(true);
		aciResponse.setResult(new Result());
		aciResponse.setMerchantTransactionId("1");
		final OrderModel order = mock(OrderModel.class);
		final PaymentTransactionModel transaction = mock(PaymentTransactionModel.class);
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);
		final PaymentTransactionEntryModel pte = mock(PaymentTransactionEntryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		//final DataPopulator dp = mock(DataPopulator.class);
		final PaymentTransactionEntryModel refundEntry = mock(PaymentTransactionEntryModel.class);
		Mockito.when(currency.getIsocode()).thenReturn("GBP");
		final Iterator<PaymentTransactionModel> iterator = mock(Iterator.class);
		final List<PaymentTransactionModel> list = mock(List.class);

		when(list.iterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn(transaction);

		Mockito.doReturn(list).when(order).getPaymentTransactions();

		final List<PaymentTransactionEntryModel> mockList = Mockito.mock(List.class);
		Mockito.doReturn(mockList).when(transaction).getEntries();

		final Iterator<PaymentTransactionEntryModel> mockIterator = Mockito.mock(Iterator.class);

		when(mockList.iterator()).thenReturn(mockIterator);
		when(mockIterator.hasNext()).thenReturn(true, false);
		Mockito.doReturn(pte).when(mockIterator).next();
		Mockito.doReturn(currency).when(pte).getCurrency();
		Mockito.doReturn(currency).when(order).getCurrency();
		Mockito.when(pte.getAmount()).thenReturn(new BigDecimal(100.00));
		Mockito.when(pte.getType()).thenReturn(PaymentTransactionType.CAPTURE);
		Mockito.doReturn(baseStore).when(order).getStore();
		Mockito.doReturn(aciConfigModel).when(baseStore).getACIConfig();

		Mockito.doNothing().when(aciTransactionService).saveTransactionResponse(aciResponse, order);
		Mockito.doReturn(refundEntry).when(aciTransactionService).createPaymentTransactionEntryModel(
				Mockito.any(PaymentTransactionModel.class), Mockito.any(ACIPaymentProcessResponse.class));
		Mockito.doReturn("1").when(order).getAciMerchantTransactionId();
		Mockito.doReturn(aciResponse).when(defaultACIPaymentService).processPaymentActions(Mockito.any(OrderModel.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		Mockito.doReturn(aciResponse).when(aciPayment).doPaymentAction(Matchers.anyMap(), Matchers.anyString(),
				Matchers.anyString());

		Mockito.doNothing().when(modelService).saveAll();
		defaultACIPaymentService.refund(order, transaction, "20");
		assertEquals(true, aciResponse.isOk());
	}

	@Test
	public void testReversal()
	{
		aciResponse = new ACIPaymentProcessResponse();
		aciResponse.setOk(true);
		aciResponse.setResult(new Result());
		final OrderModel order = mock(OrderModel.class);

		final PaymentTransactionModel transaction = mock(PaymentTransactionModel.class);
		final PaymentTransactionEntryModel pte = mock(PaymentTransactionEntryModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		final PaymentTransactionEntryModel returnEntry = mock(PaymentTransactionEntryModel.class);
		Mockito.when(currency.getIsocode()).thenReturn("GBP");
		Mockito.doReturn(currency).when(pte).getCurrency();
		Mockito.doReturn(currency).when(order).getCurrency();
		Mockito.when(pte.getAmount()).thenReturn(new BigDecimal(100.00));
		Mockito.when(pte.getPaymentTransaction()).thenReturn(transaction);

		Mockito.doNothing().when(aciTransactionService).saveTransactionResponse(aciResponse, order);
		Mockito.doReturn(returnEntry).when(aciTransactionService).createPaymentTransactionEntryModel(
				Mockito.any(PaymentTransactionModel.class), Mockito.any(ACIPaymentProcessResponse.class));
		Mockito.doReturn(aciResponse).when(defaultACIPaymentService).processPaymentActions(Mockito.any(OrderModel.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		Mockito.doNothing().when(modelService).saveAll();
		defaultACIPaymentService.refund(order, pte, "20", "GBP");
		assertEquals(true, aciResponse.isOk());
	}

	@Test
	public void testgetAciStatus()
	{


		aciResponse = new ACIPaymentProcessResponse();
		aciResponse.setOk(true);

		when(checkoutOrder.getStatus(Matchers.anyMap(), Matchers.anyString(), Matchers.anyString())).thenReturn(aciResponse);
		defaultACIPaymentService.getAciStatus("/testpayment");
		assertEquals(true, aciResponse.isOk());


	}

}
