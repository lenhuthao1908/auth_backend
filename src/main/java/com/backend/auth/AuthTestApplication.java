package com.backend.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories
public class AuthTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthTestApplication.class, args);
	}

}
