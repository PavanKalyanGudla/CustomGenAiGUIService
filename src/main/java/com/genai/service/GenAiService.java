package com.genai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genai.constants.Constants;
import com.genai.dao.GenAiDao;
import com.genai.model.User;

@Service
public class GenAiService {

	@Autowired
	private EmailService emailservice;
	
	@Autowired
	private GenAiDao dao;
	
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
}
