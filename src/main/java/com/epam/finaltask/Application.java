package com.epam.finaltask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		configureTimeZone();
		SpringApplication.run(Application.class, args);
	}

	static void configureTimeZone() {
		TimeZone.setDefault(
				TimeZone.getTimeZone("UTC")
		);
	}
}

