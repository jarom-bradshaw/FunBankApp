package com.jarom.funbankapp;

import com.jarom.funbankapp.config.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class FunBankAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(FunBankAppApplication.class, args);
	}
}

