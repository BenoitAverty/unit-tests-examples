package com.example.unit.testing.domain;

import javax.persistence.Entity;

import org.springframework.data.annotation.Id;

@Entity
public class Order {

	@Id
	private Long tid ;
	
	private String status;
	
	private Customer customer;
	
	private Long item;
	
	public Order(Customer customer, Long itemId) {
		this.customer = customer;
		this.item = itemId;
		this.status = "NEW";
	}

	public Double calculateAmount() {
		return (item.hashCode() % 1000) / 100.0;
	}
	
	public void processPayment(boolean paymentSuccess) {
		this.status = (paymentSuccess) ? "IN PROGRESS" : "PAYMENT FAILED";
	}
	
	public Long getTid() {
		return tid;
	}

	public String getStatus() {
		return status;
	}

	public Customer getCustomer() {
		return customer;
	}
	
}
