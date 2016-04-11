package com.example.unit.testing.domain;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
public interface CreditCardService {
	boolean isMaxedOut(String creditCardNumber, Double amount);

	LocalDate expirationDate(String creditCardNumber);
}
