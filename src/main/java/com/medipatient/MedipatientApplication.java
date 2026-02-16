package com.medipatient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MedipatientApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedipatientApplication.class, args);
	}

}