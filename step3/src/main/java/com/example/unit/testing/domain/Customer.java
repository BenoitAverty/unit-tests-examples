package com.example.unit.testing.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.springframework.data.annotation.Id;

@Entity
public class Customer {

	@Id
	private Long tid;
	
	private List<Order> orders = new ArrayList<>();
	
	public Long getTid() {
		return tid;
	}
	
	public Order placeOrder(Long itemId) {
		Order o = new Order(this, itemId);
		this.orders.add(o);
		return o;
	}

	/** Return orders that are not failed on closed */
	public List<Order> getProcessingOrders() {
		return orders.stream()
				.filter(o -> o.getStatus().equals("IN PROGRESS") || o.getStatus().equals("NEW"))
				.collect(Collectors.toList());
	}

}

