package com.example.unit.testing.domain.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.unit.testing.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

}
