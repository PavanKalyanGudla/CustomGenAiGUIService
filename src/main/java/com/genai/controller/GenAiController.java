package com.genai.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.genai.constants.Constants;
import com.genai.model.ChatGptRequest;
import com.genai.model.ChatTransaction;
import com.genai.model.ImageAnalysisTransaction;
import com.genai.model.ImageTransaction;
import com.genai.model.ResponseObj;
import com.genai.model.TranslationTransaction;
import com.genai.model.User;
import com.genai.service.GenAiService;

import reactor.core.publisher.Flux;

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
		return genService.signIn(email, password);
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
    public ResponseEntity<String> getChatGptResponse(@RequestBody ChatGptRequest request) {
		User user = request.getUser();
		String prompt = request.getPrompt();
        String response = genService.getChatCompletion(user,prompt);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/stream")
    public Flux<String> chatWithStream(@RequestParam String message) {
		return genService.chatWithStream(message);
    }
	
	@GetMapping("/getChatHistory")
	public Map<String, List<ChatTransaction>> getChatHistory(String email, String password){
		return genService.getChatHistory(email, password);
	}
	
	@PostMapping("/imageGptApi")
    public ResponseEntity<byte[]> generateImage(@RequestBody ChatGptRequest request) {
		byte[] responseImage = genService.generateImage(request);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
    	return new ResponseEntity<>(responseImage, headers, HttpStatus.OK);
	}
	
	@GetMapping("/getImageChatHistory")
	public Map<String, List<ImageTransaction>> getImageHistory(String email, String password){
		return genService.getImageHistory(email, password);
	}
	
	@PostMapping("/imageAnalysis")
	public String getImageAnalysis(String userId, String prompt, @RequestParam("file") MultipartFile file) {
		String response = "";
		try {
			response = genService.generateImageToText(userId,prompt, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@GetMapping("/getImageAnalysisHistory")
	public Map<String, List<ImageAnalysisTransaction>> getImageAnalysisHistory(String email, String password) {
		return genService.getImageAnalysisTransactions(email,password);
	}
	
	@GetMapping("/translate")
	public String translate(@RequestParam String userId,@RequestParam String text, @RequestParam String sourceLanguage, @RequestParam String targetLanguage) {
		return genService.translate(text, sourceLanguage, targetLanguage,userId);
	}
	
	@GetMapping("/getUserTranslations")
	public Map<String, List<TranslationTransaction>> getTranslateTransactions(@RequestParam String userId) {
		return genService.getTranslationTransaction(userId);
	}
	
	@PostMapping("/resumeAnalyzer")
    public ResponseEntity<String> analyzeResume(@RequestParam("file") MultipartFile file) {
        try {
            String resumeText = genService.extractTextFromFile(file);
            String analysis = genService.analyzeResume(resumeText);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to analyze resume");
        }
    }

}
