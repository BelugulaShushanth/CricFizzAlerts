package com.cricFizzAlerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class CricFizzAlertsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CricFizzAlertsApplication.class, args);
	}

}
