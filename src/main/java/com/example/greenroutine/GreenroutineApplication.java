package com.example.greenroutine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GreenroutineApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenroutineApplication.class, args);
	}

}
