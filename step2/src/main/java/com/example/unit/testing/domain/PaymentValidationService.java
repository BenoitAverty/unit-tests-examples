package com.example.unit.testing.domain;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentValidationService {

	@Autowired
	CreditCardService creditCardService;
	
	public boolean verifyCreditCardPayment(String creditCardNumber, Double amount) {
		return !creditCardService.isMaxedOut(creditCardNumber, amount) 
				&& creditCardService.expirationDate(creditCardNumber).compareTo(LocalDate.now().plusMonths(3)) > 0 ;
	}

}
