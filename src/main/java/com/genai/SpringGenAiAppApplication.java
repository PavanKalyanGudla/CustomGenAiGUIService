package com.genai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringGenAiAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGenAiAppApplication.class, args);
	}
	
	@Bean
	RestTemplate getResttemplate() {
		return new RestTemplate();
	}

}
