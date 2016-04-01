package com.example.unit.testing.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {

	@Id
	private Long id;
	
	public Long getTid() {
		return 0L;
	}
	
	public Order placeOrder(Long itemId) {
		return new Order(this, itemId);
	}

}

