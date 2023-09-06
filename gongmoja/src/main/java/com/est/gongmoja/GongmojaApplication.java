package com.est.gongmoja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GongmojaApplication {
	public static void main(String[] args) {
		SpringApplication. run(GongmojaApplication.class, args);
	}

}
