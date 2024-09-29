package com.genai.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	
	@PostMapping("/uploadUserProfile")
    public String uploadImage(String userId, @RequestParam("file") MultipartFile file) {
		try {
			return genService.uploadImage(userId, file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Constants.ERROR;
	}
	
	@GetMapping("/getProfilePic")
	public ResponseEntity<byte[]> getUserProfilePic(String email, String password){
		ResponseObj userResponse = signInObj(email, password);
		if(userResponse.getResponseMsg().equals(Constants.SUCCESS_MSG)) {
			User user = (User) userResponse.getResponseModel();
			byte[] profilePic = user.getProfilePic();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<>(profilePic, headers, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/chatGptApi")
    public ResponseEntity<String> getChatGptResponse(String prompt) {
        String response = genService.getChatCompletion(prompt);
        return ResponseEntity.ok(response);
    }
}
