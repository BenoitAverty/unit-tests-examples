package com.example.unit.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.unit.testing.application.OrderApplication;

@SpringBootApplication
@EnableJpaRepositories("com.example.unit.testing.domain.repositories")
public class Demo {
	
	@Autowired
	private OrderApplication orderApplication;
	
	public static void main(String[] args) {
		SpringApplication.run(Demo.class, args);
	}
}
