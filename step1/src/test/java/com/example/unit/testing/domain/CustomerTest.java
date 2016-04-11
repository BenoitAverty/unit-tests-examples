package com.example.unit.testing.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CustomerTest {
	
	@Test
	public void testPlaceOrder() {
		
		Customer c = new Customer();
		
		Order o = c.placeOrder(9999L);
		
		assertThat(o).isNotNull();
		assertThat(o.getStatus()).isEqualTo("NEW");
	}
	
	@Test
	public void testGetProcessingOrders() {
		
		Customer c = new Customer();
		
		assertThat(c.getProcessingOrders()).isEmpty();
		
		Order o = c.placeOrder(0L);
		
		assertThat(c.getProcessingOrders().size()).isEqualTo(1);
		
		o.processPayment(true);
		assertThat(c.getProcessingOrders().size()).isEqualTo(1);
		
		o.processPayment(false);
		assertThat(c.getProcessingOrders()).isEmpty();
	}
	
}
