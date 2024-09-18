package com.genai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenAiController {

	@GetMapping("/verify")
	public String healthCheck() {
		return "Health Green";
	}
}
