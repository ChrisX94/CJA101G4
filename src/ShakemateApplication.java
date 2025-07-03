package com.shakemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = "com.shakemate")
@EntityScan(basePackages = "com.shakemate")
@SpringBootApplication(scanBasePackages = "com.shakemate")

public class ShakemateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShakemateApplication.class, args);
	}

}
