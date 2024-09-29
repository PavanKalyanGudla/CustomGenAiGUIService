package com.genai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.genai.constants.Constants;
import com.genai.dao.GenAiDao;
import com.genai.model.User;

@Service
public class GenAiService {

	@Value("${openai.api.url}")
	private String apiUrl;
	
	@Value("${spring.ai.openai.api-key}")
	private String apiKey;
	
	@Autowired
	private EmailService emailservice;
	
	@Autowired
	private GenAiDao dao;
	
	@Autowired
	private RestTemplate restTemplate;
	
	GenAiService(RestTemplate restTemplate){
		this.restTemplate = restTemplate;
	}
	
	public String addUserRegistration(User user) {
		String response = dao.addUserRegistration(user);
		if(response.equals(Constants.USER_REGISTRATION_SUCCESS)) {
			emailservice.sendSimpleEmail(user.getEmail(), Constants.EMAIL_Registration_SUCCESSFUL_SUBJECT, 
					Constants.getUserRegistrationBody(user.getFirstName(), user.getLastName(), user.getUserId(),
							user.getEmail(), user.getDateOfJoin()));
		}
		return response;
	}
	
	public String forgotPassword(String email){
		if(null != email) {
			int userCount = dao.getUserCount(email.split("@")[0]);
			if(userCount > 0) {
				String password = dao.getUserPassword(email.split("@")[0]);
				emailservice.sendSimpleEmail(email, Constants.FORGOT_PASSWORD, Constants.forgotpassword(password));
				return Constants.CHECK_MAIL;
			}else {
				return Constants.USER_NOT_EXISTS;
			}
		}
		return Constants.ERROR;
	}
	
	public User signIn(String email, String password) {
		User userObject = dao.getUserObject(email,password);
		return userObject;
	}
	
	public String uploadImage(String userId, byte[] image) {
		if(dao.getUserCount(userId) > 0) {
			int count = dao.updateProfilePic(userId, image);
			if(count > 0) {
				return Constants.SUCCESS_MSG;
			}else {
				return Constants.FAILURE_MSG;
			}
		}else {
			return Constants.ACCOUNT_DOESNT_EXISTS;
		}
	}
	
	public String getChatCompletion(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("messages", List.of(
            Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error calling OpenAI API: " + response.getStatusCode());
        }
    }
}
