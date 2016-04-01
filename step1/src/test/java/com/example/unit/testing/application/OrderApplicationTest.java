package com.example.unit.testing.application;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.*;

import com.example.unit.testing.domain.Customer;
import com.example.unit.testing.domain.Order;
import com.example.unit.testing.domain.PaymentValidationService;
import com.example.unit.testing.domain.repositories.CustomerRepository;
import com.example.unit.testing.domain.repositories.OrderRepository;

public class OrderApplicationTest {
	
	@Mock
	CustomerRepository customerRepository;
	
	@Mock
	OrderRepository orderRepository;
	
	@Mock
	PaymentValidationService paymentValidationService;
	
	@InjectMocks
	OrderApplication orderApplication = new OrderApplication();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void placeOrderTest() {
		
		Order o = mock(Order.class);
		when(o.calculateAmount()).thenReturn(10.0);
		
		Customer customerMock = mock(Customer.class);
		when(customerMock.placeOrder(0L)).thenReturn(o);
		
		when(customerRepository.findOne(0L)).thenReturn(customerMock);
		
		when(paymentValidationService.verifyCreditCardPayment("123", 10.0)).thenReturn(true);
		
		Long result = orderApplication.placeOrder(0L, 0L, "123");
		
		ArgumentCaptor<Customer> customerArg = ArgumentCaptor.forClass(Customer.class);
		ArgumentCaptor<Order> orderArg = ArgumentCaptor.forClass(Order.class);
		verify(customerRepository).save(customerArg.capture());
		verify(orderRepository).save(o);
		
		assertThat(result).isEqualTo(0l);
	}
	
	@Test
	public void getOrderStatusTest() {
		// given
		Order o = mock(Order.class);
		when(o.getStatus()).thenReturn("IN PROGRESS");
		
		when(orderRepository.findOne(0L)).thenReturn(o);
		
		// when
		String result = orderApplication.getOrderStatus(0L);
		
		// then
		assertThat(result).isEqualTo("IN PROGRESS");
	}
}
