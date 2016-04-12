package com.example.unit.testing.application;

import static com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder.builder;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.example.unit.testing.domain.CreditCardService;
import com.example.unit.testing.domain.Customer;
import com.example.unit.testing.domain.Order;
import com.example.unit.testing.domain.repositories.CustomerRepository;
import com.example.unit.testing.domain.repositories.OrderRepository;
import com.nitorcreations.junit.runners.NestedRunner;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
@ContextConfiguration(
		classes = OrderApplicationTestContext.class,
		loader = SpringApplicationContextLoader.class)
public class PlacingOrderTest {

	@Before
	public void setUp() throws Exception {
		new TestContextManager(this.getClass()).prepareTestInstance(this);
	}

	// System under test
	@Autowired
	OrderApplication orderApplication;

	// mocks
	@Autowired
	CustomerRepository customerRepositoryMock;

	@Autowired
	CreditCardService creditCardServiceMock;

	public class WithExistingCustomer {
		
		private Long customerId;
		@Before
		public void beforeEach() {
			Customer c = new Customer();
			customerRepositoryMock.save(c);
			customerId = c.getTid();
		}
		
		@Test
		public void shouldReturnNewOrderId() {
			Long result = orderApplication.placeOrder(9999L, customerId, "1234");
			
			assertThat(result).isNotNull();
		}

		public class WhenCCIsNotMaxedOutNorExpired {

			@Before
			public void beforeEach() {
				Mockito.when(creditCardServiceMock.isMaxedOut("123", 1.23)).thenReturn(false);
				Mockito.when(creditCardServiceMock.expirationDate("123")).thenReturn(LocalDate.MAX);
			}

			@Test
			public void shouldCreateAnOrderWithStatusInProgress() {
				Long orderId = orderApplication.placeOrder(123L, customerId, "123");
				String orderStatus = orderApplication.getOrderStatus(orderId);

				assertThat(orderStatus).isEqualTo("IN PROGRESS");
			}

			@Test
			public void shouldMakeOrderAvailableThroughCustomer() {
				Long orderId = orderApplication.placeOrder(123L, customerId, "123");
				List<Order> orders = orderApplication.findOrdersForCustomer(customerId);

				assertThat(orders).isNotEmpty();
				assertThat(orders).extracting(o -> o.getTid()).contains(orderId);
			}
		}

		public class WhenCCIsMaxedOut {

			@Before
			public void beforeEach() {
				Mockito.when(creditCardServiceMock.isMaxedOut("123", 1.23)).thenReturn(true);
			}

			@Test
			public void shouldCreateAnOrderWithStatusPaymentFailed() {
				Long orderId = orderApplication.placeOrder(123L, customerId, "123");
				String orderStatus = orderApplication.getOrderStatus(orderId);

				assertThat(orderStatus).isEqualTo("PAYMENT FAILED");
			}

			@Test
			public void shouldNotMakeOrderAvailableThroughCustomer() {
				Long orderId = orderApplication.placeOrder(123L, customerId, "123");
				List<Order> orders = orderApplication.findOrdersForCustomer(customerId);

				assertThat(orders).extracting(o -> o.getTid()).doesNotContain(orderId);
			}
		}

		public class WhenCCExpiresInLessThanThreeMonths {

			@Before
			public void beforeEach() {
				Mockito.when(creditCardServiceMock.isMaxedOut("123", 1.23)).thenReturn(false);
				Mockito.when(creditCardServiceMock.expirationDate("123")).thenReturn(LocalDate.now().plusMonths(1));
			}

			@Test
			public void shouldCreateAnOrderWithStatusPaymentFailed() {
				Long orderId = orderApplication.placeOrder(123L, customerId, "123");
				String orderStatus = orderApplication.getOrderStatus(orderId);

				assertThat(orderStatus).isEqualTo("PAYMENT FAILED");
			}

			@Test
			public void shouldNotMakeOrderAvailableThroughCustomer() {
				Long orderId = orderApplication.placeOrder(123L, customerId, "123");
				List<Order> orders = orderApplication.findOrdersForCustomer(customerId);

				assertThat(orders).extracting(o -> o.getTid()).doesNotContain(orderId);
			}
		}
		
	}

}

/**
 * Configuration class for the spring test context of OrderApplication Test.
 * <ul>
 * <li>@Import real OrderApplication so it is available to the test class</li>
 * <li>Scan the "domain" package to configure domain services (needed as
 * dependencies of OrderApplication).</li>
 * <li>Provide infrastructure services as mocks.</li>
 * <li>Use spring data map repositories instead of JPA repositories</li>
 * </ul>
 * 
 * note that we DON'T include the "application" package in the ComponentScan
 * because we don't need other application services. They don't depend on each
 * other. We also don't put the @EnableJpaRepositories annotation, because we
 * mock repositories. This isn't integration testing, we don't load any
 * infrastructure layer.
 */
@Configuration
@Import(OrderApplication.class)
@ComponentScan("com.example.unit.testing.domain")
class OrderApplicationTestContext {

	@Bean
	public CreditCardService creditCardService() {
		return Mockito.mock(CreditCardService.class);
	}

	@Bean
	public OrderRepository orderRepository() {
		return builder().mock(OrderRepository.class);
	}

	@Bean
	public CustomerRepository customerRepository() {
		return builder().mock(CustomerRepository.class);
	}

}
