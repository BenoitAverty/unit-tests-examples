package com.example.unit.testing.domain;

import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PaymentValidationServiceTest {

	
	@InjectMocks
	PaymentValidationService paymentValidationService;
	
	@Mock
	CreditCardService creditCardService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testVerifyCreditCardPayment() {
		
		when(creditCardService.isMaxedOut("123", 10.0)).thenReturn(false);
		when(creditCardService.expirationDate("123")).thenReturn(LocalDate.MAX);
		
		boolean result = paymentValidationService.verifyCreditCardPayment("123", 10.0);
		
		assertThat(result).isTrue();
		
	}
	
	@Test
	public void testVerifyCreditCardPaymentMaxedOut() {
		
		when(creditCardService.isMaxedOut("123", 10.0)).thenReturn(true);
		when(creditCardService.expirationDate("123")).thenReturn(LocalDate.MAX);
		
		boolean result = paymentValidationService.verifyCreditCardPayment("123", 10.0);
		
		assertThat(result).isFalse();
		
	}
	
	@Test
	public void testVerifyCreditCardPaymentCardExpired() {
		
		when(creditCardService.isMaxedOut("123", 10.0)).thenReturn(true);
		when(creditCardService.expirationDate("123")).thenReturn(LocalDate.now().plusMonths(2));
		
		boolean result = paymentValidationService.verifyCreditCardPayment("123", 10.0);
		
		assertThat(result).isFalse();
		
	}
}