package com.eazybytes.springsecsection1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = true)
public class Springsecsection1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springsecsection1Application.class, args);
		System.out.println("===================");
		System.out.println("Application started");
		System.out.println("===================");
	}

}
