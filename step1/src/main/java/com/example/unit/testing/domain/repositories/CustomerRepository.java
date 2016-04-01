package com.example.unit.testing.domain.repositories;

import org.springframework.data.repository.CrudRepository;

import com.example.unit.testing.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

}
