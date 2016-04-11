package com.example.unit.testing.application;

import static com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder.builder;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.unit.testing.domain.CreditCardService;
import com.example.unit.testing.domain.Customer;
import com.example.unit.testing.domain.Order;
import com.example.unit.testing.domain.repositories.CustomerRepository;
import com.example.unit.testing.domain.repositories.OrderRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(OrderApplicationTestContext.class)
public class OrderApplicationTest {

	// System under test
	@Autowired
	OrderApplication orderApplication;

	// mocks
	@Autowired
	CustomerRepository customerRepositoryMock;

	@Autowired
	CreditCardService creditCardServiceMock;
	
	@Test
	public void placeOrderNominal() {

		// Given : there is a customer in the system and the creditcardService says ok.
		Customer customer = new Customer();
		customerRepositoryMock.save(customer);
		Mockito.when(creditCardServiceMock.isMaxedOut(Mockito.anyString(), Mockito.anyDouble())).thenReturn(false);
		Mockito.when(creditCardServiceMock.expirationDate(Mockito.anyString())).thenReturn(LocalDate.MAX);

		// When : we place an order
		Long result = orderApplication.placeOrder(0L, customer.getTid(), "123");

		// Then : The order ID is returned, the order can be retrieved for the
		// customer, the order is created with the status "in progress"
		String newOrderStatus = orderApplication.getOrderStatus(result);
		List<Order> ordersForCustomer = orderApplication.findOrdersForCustomer(customer.getTid());

		assertThat(result).isNotNull();
		assertThat(newOrderStatus).isEqualTo("IN PROGRESS");
		assertThat(ordersForCustomer).isNotEmpty();
		assertThat(ordersForCustomer.get(0).getTid()).isEqualTo(result);
	}
	
	@Test
	public void placeOrderCCMaxedOut() {

		// Given : there is a customer in the system and the credit card is maxed out
		Customer customer = new Customer();
		customerRepositoryMock.save(customer);
		Mockito.when(creditCardServiceMock.isMaxedOut(Mockito.anyString(), Mockito.anyDouble())).thenReturn(true);

		// When : we place an order
		Long result = orderApplication.placeOrder(0L, customer.getTid(), "123");

		// Then : The order ID is returned, the order cant't be retrieved for the
		// customer, the order is created with the status "PAYMENT FAILED"
		String newOrderStatus = orderApplication.getOrderStatus(result);
		List<Order> ordersForCustomer = orderApplication.findOrdersForCustomer(customer.getTid());

		assertThat(result).isNotNull();
		assertThat(newOrderStatus).isEqualTo("PAYMENT FAILED");
		assertThat(ordersForCustomer).isEmpty();
	}
	
	@Test
	public void placeOrderCCExpired() {

		// Given : there is a customer in the system and the credit card is maxed out
		Customer customer = new Customer();
		customerRepositoryMock.save(customer);
		Mockito.when(creditCardServiceMock.isMaxedOut(Mockito.anyString(), Mockito.anyDouble())).thenReturn(false);
		Mockito.when(creditCardServiceMock.expirationDate(Mockito.anyString())).thenReturn(LocalDate.now().plusMonths(2));

		// When : we place an order
		Long result = orderApplication.placeOrder(0L, customer.getTid(), "123");

		// Then : The order ID is returned, the order cant't be retrieved for the
		// customer, the order is created with the status "PAYMENT FAILED"
		String newOrderStatus = orderApplication.getOrderStatus(result);
		List<Order> ordersForCustomer = orderApplication.findOrdersForCustomer(customer.getTid());

		assertThat(result).isNotNull();
		assertThat(newOrderStatus).isEqualTo("PAYMENT FAILED");
		assertThat(ordersForCustomer).isEmpty();
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
