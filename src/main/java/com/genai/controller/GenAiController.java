package com.genai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.genai.constants.Constants;
import com.genai.model.ResponseObj;
import com.genai.model.User;
import com.genai.service.GenAiService;

@RestController
@CrossOrigin("http://localhost:4200")
public class GenAiController {
	
	@Autowired
	private GenAiService genService;
	
	@GetMapping("/verify")
	public String healthCheck() {
		return "Health Green";
	}
	
	@PostMapping("/addUser")
	public String addUserRegistration(@RequestBody User user) {
		return genService.addUserRegistration(user);
	}
	
	@GetMapping("/forgotPassword")
	public String forgotPassword(String email){
		return genService.forgotPassword(email);
	}
	
//	@GetMapping("/loginUser")
//	public User signIn(String email, String password) {
//		return genService.signIn(email, password);
//	}
	
	@GetMapping("/loginUser")
	public ResponseObj signInObj(String email, String password) {
		ResponseObj obj = new ResponseObj();
		User userObj = genService.signIn(email, password);
		obj.setResponseModel(userObj);
		if(null != userObj) {
			obj.setResponseCode(Constants.SUCCESS_CODE);
			obj.setResponseMsg(Constants.SUCCESS_MSG);
		}else {
			obj.setResponseCode(Constants.FAILURE_CODE);
			obj.setResponseMsg(Constants.FAILURE_MSG);
		}
		return obj;
	}
}
