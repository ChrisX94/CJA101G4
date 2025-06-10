package com.shakemate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.shakemate.user.dao")
@EntityScan(basePackages = "com.shakemate.user.model")
@SpringBootApplication
public class ShakemateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShakemateApplication.class, args);
	}

}
