/*
 *  ACI Hybris Extension
 *
 */
package com.hybris.aci.dao;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.daos.OrderDaoTest;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.internal.model.order.InMemoryCartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.Assert;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.fest.assertions.GenericAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ACIOrderDaoTest extends ServicelayerTransactionalTest
{
	private final static Logger LOG = Logger.getLogger(OrderDaoTest.class);

	private static final String DEFAULT_CART = "";
	private static final String IN_MEMORY_CART = InMemoryCartModel._TYPECODE;

	private static final int NOT_EXISTING_ORDER_ENTRY_NUMBER = 0;
	private static final int FIRST_ORDER_ENTRY_NUMBER = NOT_EXISTING_ORDER_ENTRY_NUMBER + 11;
	private static final int SECOND_ORDER_ENTRY_NUMBER = FIRST_ORDER_ENTRY_NUMBER + 11;
	private static final int THIRD_ORDER_ENTRY_NUMBER = SECOND_ORDER_ENTRY_NUMBER + 11;

	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ACIOrderDao aciOrderDao;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private ConfigurationService configurationService;

	private CartModel cart;
	private OrderModel order;
	private ProductModel product;
	private ProductModel otherProduct;
	private ProductModel unknownProduct;
	private DebitPaymentInfoModel paymentInfo;
	private AddressModel deliveryAddress;
	private CurrencyModel myCurrency;
	private DeliveryModeModel deliveryMode;
	private PaymentModeModel paymentMode;
	private UnitModel unit;
	private String cartTypeToRestore;

	@Before
	public void setUp()
	{
		LOG.info("### Before -> Session cart type: " + jaloSession.getSessionContext().getAttribute(JaloSession.CART_TYPE)
				+ " configured cart " + jaloSession.getTenant().getConfig().getParameter(JaloSession.CART_TYPE));
		cartTypeToRestore = (String) configurationService.getConfiguration().getProperty(JaloSession.CART_TYPE);
		LOG.info("### Before -> Cart type to restore -> " + cartTypeToRestore);
	}

	@After
	public void tearDown()
	{
		LOG.info("### After -> Cart type to restore -> " + cartTypeToRestore);
		configureCartTypeTo(cartTypeToRestore);
		LOG.info("### After -> Session cart type: " + jaloSession.getSessionContext().getAttribute(JaloSession.CART_TYPE)
				+ " configured cart " + jaloSession.getTenant().getConfig().getParameter(JaloSession.CART_TYPE));
	}

	@Test
	public void testOrderByAciPaymentIdFound() throws InvalidCartException
	{
		setUpWithCartType(DEFAULT_CART);
		testFindOrderByAciPaymentIdFound();
	}

	/*
	 * @Test public void testOrderByAciPaymentIdNoFound() throws InvalidCartException { setUpWithCartType(DEFAULT_CART);
	 * testFindOrderByAciPaymentIdNotFound(); }
	 * 
	 * @Test public void testPendingOrdersFound() throws InvalidCartException { setUpWithCartType(DEFAULT_CART);
	 * testFindPendingOrdersFound(); }
	 * 
	 * @Test public void testPendingOrdersNotFound() throws InvalidCartException { setUpWithCartType(DEFAULT_CART);
	 * testFindPendingOrdersNotFound(); }
	 * 
	 * @Test public void testOrderModelFound() throws InvalidCartException { setUpWithCartType(DEFAULT_CART);
	 * testFindOrderModelFound(); }
	 * 
	 * @Test public void testOrderModelNOTFound() throws InvalidCartException { setUpWithCartType(DEFAULT_CART);
	 * testFindOrderModelNotFound(); }
	 */
	private void testFindOrderByAciPaymentIdFound() throws InvalidCartException
	{

		cartService.addToCart(cart, product, 2, null);
		cart.setAciPaymentId("ACI_1");
		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		assertEquals("Orders ACI Payment id is as expected", "ACI_1", order.getAciPaymentId());
		final List<OrderModel> fetchedOrders = aciOrderDao.findOrderByAciPaymentId("ACI_1");
		Assert.assertCollectionElements(fetchedOrders, order);
	}

	private void testFindOrderByAciPaymentIdNotFound() throws InvalidCartException
	{
		cartService.addToCart(cart, product, 2, null);
		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		Assert.assertNotEquals("Orders ACI Payment id is not as expected", order.getAciPaymentId(), "ACI_1");
		final List<OrderModel> fetchedOrders = aciOrderDao.findOrderByAciPaymentId("ACI_1");
		assertTrue("Fetched collection is expected to be empty", fetchedOrders.isEmpty());
	}

	private void testFindPendingOrdersFound() throws InvalidCartException
	{

		cartService.addToCart(cart, product, 2, null);

		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		order.setStatus(OrderStatus.WAITING_ACI_PAYMENT);
		modelService.save(order);
		assertEquals("Orders ACI Payment status is as expected", OrderStatus.WAITING_ACI_PAYMENT, order.getStatus());
		final List<OrderModel> fetchedOrders = aciOrderDao.findPendingOrders();
		Assert.assertCollectionElements(fetchedOrders, order);
	}

	private void testFindPendingOrdersNotFound() throws InvalidCartException
	{
		cartService.addToCart(cart, product, 2, null);
		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		Assert.assertNotEquals("Orders ACI Payment Status is not as expected", OrderStatus.WAITING_ACI_PAYMENT, order.getStatus());
		final List<OrderModel> fetchedOrders = aciOrderDao.findPendingOrders();
		assertTrue("Fetched collection is expected to be empty", fetchedOrders.isEmpty());
	}

	private void testFindOrderModelFound() throws InvalidCartException
	{

		cartService.addToCart(cart, product, 2, null);

		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		order.setCode("0011");
		modelService.save(order);
		assertEquals("Order Found ", "0011", order.getCode());
		final OrderModel fetchedOrders = aciOrderDao.findOrderModel("0011");
		Assert.assertEquals(fetchedOrders, order);
	}

	private void testFindOrderModelNotFound() throws InvalidCartException
	{
		cartService.addToCart(cart, product, 2, null);
		order = orderService.placeOrder(cart, deliveryAddress, null, paymentInfo);
		final OrderModel fetchedOrders = aciOrderDao.findOrderModel("0011");
		assertNull("Fetched collection is expected to be empty", fetchedOrders);
	}


	private void setUpWithCartType(final String cartType)
	{
		configureCartTypeTo(cartType);
		try
		{
			createCoreData();
			createDefaultCatalog();
		}
		catch (final Exception e)
		{
			org.junit.Assert.fail(e.getMessage());
		}

		product = productService.getProductForCode("testProduct0");
		otherProduct = productService.getProductForCode("testProduct1");
		unknownProduct = productService.getProductForCode("testProduct3");
		cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();

		deliveryAddress = modelService.create(AddressModel.class);
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");
		paymentInfo = modelService.create(DebitPaymentInfoModel.class);
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("MeineBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");

		myCurrency = modelService.create(CurrencyModel.class);
		myCurrency.setActive(Boolean.TRUE);
		myCurrency.setIsocode("MCURR");
		myCurrency.setName("mYCurrency");
		myCurrency.setSymbol("mc");
		myCurrency.setConversion(Double.valueOf(1.3));
		modelService.save(myCurrency);

		deliveryMode = modelService.create(DeliveryModeModel.class);
		deliveryMode.setActive(Boolean.TRUE);
		deliveryMode.setCode("myTestDeliveryMode");
		deliveryMode.setName("my delivery mode");
		modelService.save(deliveryMode);

		paymentMode = modelService.create(PaymentModeModel.class);
		paymentMode.setActive(Boolean.TRUE);
		paymentMode.setCode("myTestPaymentMode");
		paymentMode.setName("my payment mode");
		paymentMode.setPaymentInfoType(typeService.getComposedTypeForCode(DebitPaymentInfoModel._TYPECODE));
		modelService.save(paymentMode);

		unit = modelService.create(UnitModel.class);
		unit.setCode("uN");
		unit.setUnitType("uT");
		modelService.save(unit);


	}

	private void configureCartTypeTo(final String cartType)
	{
		configurationService.getConfiguration().setProperty(JaloSession.CART_TYPE, cartType);
		jaloSession.getTenant().getConfig().setParameter(JaloSession.CART_TYPE, cartType);
	}

	private CartModel givenCartWithEntries()
	{
		cartService.addNewEntry(cart, product, 1, unit, FIRST_ORDER_ENTRY_NUMBER, false);
		cartService.addNewEntry(cart, otherProduct, 2, unit, SECOND_ORDER_ENTRY_NUMBER, false);
		cartService.addNewEntry(cart, product, 3, unit, THIRD_ORDER_ENTRY_NUMBER, false);
		modelService.save(cart);
		return cart;
	}

	private AbstractOrderEntryModelAssert assertThatEntry(final AbstractOrderEntryModel model)
	{
		return new AbstractOrderEntryModelAssert(Objects.requireNonNull(model));
	}

	private static class AbstractOrderEntryModelAssert extends
			GenericAssert<AbstractOrderEntryModelAssert, AbstractOrderEntryModel>
	{
		private final AbstractOrderEntryModel model;

		public AbstractOrderEntryModelAssert(final AbstractOrderEntryModel actual)
		{
			super(AbstractOrderEntryModelAssert.class, actual);
			this.model = actual;
		}

		public AbstractOrderEntryModelAssert hasProduct(final ProductModel product)
		{
			isNotNull();
			assertThat(model.getProduct()).isEqualTo(product);
			return this;
		}

		public AbstractOrderEntryModelAssert hasEntryNumber(final int number)
		{
			isNotNull();
			assertThat(model.getEntryNumber()).isEqualTo(number);
			return this;
		}
	}
}
