package com.genai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SpringGenAiAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGenAiAppApplication.class, args);
	}
	
	@Bean
	RestTemplate getResttemplate() {
		return new RestTemplate();
	}
	
	@Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

}
