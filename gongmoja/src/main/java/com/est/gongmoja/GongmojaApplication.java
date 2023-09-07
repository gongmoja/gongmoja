package com.est.gongmoja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@EnableScheduling
@SpringBootApplication
public class GongmojaApplication {
	public static void main(String[] args) {
		SpringApplication. run(GongmojaApplication.class, args);
	}
}
