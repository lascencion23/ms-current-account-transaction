package com.everis.mstransactioncurrentaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MsTransactionCurrentAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransactionCurrentAccountApplication.class, args);
	}

}
