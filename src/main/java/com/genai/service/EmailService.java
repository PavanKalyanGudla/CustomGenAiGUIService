package com.genai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Value("spring.mail.username")
	private String email;
	
	@Autowired
	private JavaMailSender sender;
	
	public void sendSimpleEmail(String toEmail,
            String subject,
            String body
		) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(email);
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		sender.send(message);
	}
}
