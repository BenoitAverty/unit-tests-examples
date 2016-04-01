package com.example.unit.testing.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Order {

	@Id
	private Long tid;
	
	private String status;
	
	private Customer customer;
	
	private Long item;
	
	public Order(Customer customer, Long itemId) {
		this.customer = customer;
		this.item = itemId;
		this.status = "IN PROGRESS";
	}

	public Double calculateAmount() {
		return 10.0;
	}
	
	public Long getTid() {
		return tid;
	}

	public String getStatus() {
		return status;
	}
	
}
