package com.example.unit.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.unit.testing.domain.repositories")
public class Demo {
	
	public static void main(String[] args) {
		SpringApplication.run(Demo.class, args);
	}
}
