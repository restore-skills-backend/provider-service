package com.restore.providerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@SpringBootApplication(exclude = {
//		DataSourceAutoConfiguration.class,
//		DataSourceTransactionManagerAutoConfiguration.class,
//		HibernateJpaAutoConfiguration.class,
//		SpringDataWebAutoConfiguration.class
//})
@SpringBootApplication
@EnableFeignClients
public class ProviderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderServiceApplication.class, args);
	}

}
