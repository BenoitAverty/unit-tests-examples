package com.example.unit.testing.application;

import java.util.List;

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
		
		boolean paymentSuccess = paymentValidationService.verifyCreditCardPayment(creditCardNumber, newOrder.calculateAmount());
		newOrder.processPayment(paymentSuccess);
		
		orderRepository.save(newOrder);
		customerRepository.save(customer);
		
		return newOrder.getTid();
	}
	
	public String getOrderStatus(Long orderId) {	
		Order order = orderRepository.findOne(orderId);
		return order.getStatus();	
	}

	public List<Order> findOrdersForCustomer(Long tid) {
		
		return customerRepository.findOne(tid).getProcessingOrders();
	}
}
