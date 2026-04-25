package com.example.billing_support_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BillingSupportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingSupportServiceApplication.class, args);
	}

}
