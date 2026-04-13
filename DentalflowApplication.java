package com.dentalflow.dentalflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DentalflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentalflowApplication.class, args);
	}

}
