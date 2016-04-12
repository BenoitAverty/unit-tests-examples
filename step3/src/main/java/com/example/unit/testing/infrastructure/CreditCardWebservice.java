package com.example.unit.testing.infrastructure;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.unit.testing.domain.CreditCardService;

@Service
public class CreditCardWebservice implements CreditCardService{

	@Override
	public boolean isMaxedOut(String creditCardNumber, Double amount) {
		return false;
	}

	@Override
	public LocalDate expirationDate(String creditCardNumber) {
		return LocalDate.of(2018, 01, 01);
	}
}
