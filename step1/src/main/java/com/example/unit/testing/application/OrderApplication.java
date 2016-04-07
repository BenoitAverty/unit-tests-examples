package com.example.unit.testing.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.unit.testing.domain.Customer;
import com.example.unit.testing.domain.Order;
import com.example.unit.testing.domain.PaymentValidationService;
import com.example.unit.testing.domain.repositories.CustomerRepository;
import com.example.unit.testing.domain.repositories.OrderRepository;

@Service
public class OrderApplication {

	private Logger log = LoggerFactory.getLogger(OrderApplication.class);
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired 
	OrderRepository orderRepository;
	
	@Autowired
	PaymentValidationService paymentValidationService;
	
	
	@Transactional
	public Long placeOrder(Long itemId, Long customerId, String creditCardNumber) {
		
		Customer customer = customerRepository.findOne(customerId);
		Order newOrder = customer.placeOrder(itemId);
		
		paymentValidationService.verifyCreditCardPayment(creditCardNumber, newOrder.calculateAmount());
		
		orderRepository.save(newOrder);
		customerRepository.save(customer);
		
		return newOrder.getTid();
	}
	
	public String getOrderStatus(Long orderId) {	
		Order order = orderRepository.findOne(orderId);
		return order.getStatus();	
	}
}
