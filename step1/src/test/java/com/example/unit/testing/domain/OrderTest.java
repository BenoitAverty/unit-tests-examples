package com.example.unit.testing.domain;

import org.junit.Test;import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {
	
	@Test
	public void testCalculateAmount() {
		
		Customer c = new Customer();
		Order o = new Order(c, 9999L);
		
		assertThat(o.calculateAmount()).isEqualTo(9.99);
		
	}
	
	@Test
	public void testProcessPayment() {
		Customer c = new Customer();
		Order o = new Order(c, 9999L);
		
		o.processPayment(true);
		assertThat(o.getStatus()).isEqualTo("IN PROGRESS");
		
		o.processPayment(false);
		assertThat(o.getStatus()).isEqualTo("PAYMENT FAILED");
	}
	
}
