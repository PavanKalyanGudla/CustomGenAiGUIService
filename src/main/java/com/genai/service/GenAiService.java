package com.genai.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.constants.Constants;
import com.genai.dao.GenAiDao;
import com.genai.model.ChatGptRequest;
import com.genai.model.ChatTransaction;
import com.genai.model.GifTransaction;
import com.genai.model.ImageAnalysisTransaction;
import com.genai.model.ImageTransaction;
import com.genai.model.PromptRequest;
import com.genai.model.ResponseObj;
import com.genai.model.ResumeAnalysisTransaction;
import com.genai.model.TranslationTransaction;
import com.genai.model.User;

import reactor.core.publisher.Flux;

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
	
	@Autowired
    private OpenAiImageModel openAiImageModel;
	
	@Autowired
    private ChatModel chatModel;
	
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
	
	public ResponseObj updateUserProfile(User user) {
		ResponseObj obj = new ResponseObj();
		String response = dao.addUpdateUserInfo(user);
		if(response.equals(Constants.SUCCESS_MSG)) {
			User userObj = dao.getUserObject(user.getEmail(),user.getPassword());
			obj.setResponseModel(userObj);
			if(null != userObj) {
				obj.setResponseCode(Constants.SUCCESS_CODE);
				obj.setResponseMsg(Constants.USER_SAVE_SUCCESS);
			}else {
				obj.setResponseCode(Constants.FAILURE_CODE);
				obj.setResponseMsg(Constants.USER_SAVE_SUCCESS);
			}
		}
		return obj;
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
	
	public ResponseObj signIn(String email, String password) {
		ResponseObj obj = new ResponseObj();
		if(dao.getUserCount(email.split("@")[0]) > 0) {
			User userObj = dao.getUserObject(email,password);
			obj.setResponseModel(userObj);
			if(null != userObj) {
				obj.setResponseCode(Constants.SUCCESS_CODE);
				obj.setResponseMsg(Constants.SUCCESS_MSG);
			}else {
				obj.setResponseCode(Constants.FAILURE_CODE);
				obj.setResponseMsg(Constants.INVALID_PASSWORD);
			}
		}else {
			obj.setResponseCode(Constants.FAILURE_CODE);
			obj.setResponseMsg(Constants.USER_NOT_EXISTS);
		}
		return obj;
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
	
	public String getChatCompletion(User user,String prompt) {
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
        	String resp = response.getBody();
        	String answer = getCharResponseAnswer(resp);
        	ChatTransaction transaction = new ChatTransaction();
        	transaction.setUserid(user.getUserId());
        	transaction.setQuestion(prompt);
        	transaction.setAnswer(answer);
        	dao.saveChatTransaction(transaction);
            return answer;
        } else {
            throw new RuntimeException("Error calling OpenAI API: " + response.getStatusCode());
        }
    }
	
	public String getCharResponseAnswer(String response){
		String answer = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(response);
			answer = node.get("choices").get(0).get("message").get("content").asText();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	public Map<String, List<ChatTransaction>> getChatHistory(String email, String password){
		List<ChatTransaction> chatTransactions = null;
		Map<String, List<ChatTransaction>> mapByDate = null;
		User user = dao.getUserObject(email,password);
		if(null != user) {
			String[] email1 = email.split("@");
			chatTransactions = dao.getChatTransactions(email1[0]);
			if(!chatTransactions.isEmpty()) {
				mapByDate = chatTransactions.stream().collect(Collectors.groupingBy(i -> i.getDateOfChat()));
			}
		}else {
			//User Not Exists
		}
		return mapByDate;
	}
	
	public Map<String, List<ImageTransaction>> getImageHistory(String email, String password){
		List<ImageTransaction> imageTransactions = null;
		Map<String, List<ImageTransaction>> mapByDate = null;
		User user = dao.getUserObject(email,password);
		if(null != user) {
			imageTransactions = dao.getImageTransactions(email.split("@")[0]);
			if(!imageTransactions.isEmpty()) {
				mapByDate = imageTransactions.stream().collect(Collectors.groupingBy(i -> i.getDateOfChat()));
			}
		}
		return mapByDate;
	}
	
	public byte[] generateImage(ChatGptRequest request){
		String prompt = request.getPrompt();
		ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .withHeight(1024)
                                .withQuality("hd")
                                .withWidth(1024)
                                .withN(1)
                                .build())
				);
		byte[] imageInBytes = null;
		User user = request.getUser();
		try {
			imageInBytes = getImageInBytes(response.getResult().getOutput().getUrl());
			if(null != imageInBytes) {
				ImageTransaction transaction = new ImageTransaction();
				transaction.setUserid(user.getUserId());
				transaction.setQuestion(prompt);
				transaction.setAnswer(imageInBytes);
				dao.saveImageGptTransaction(transaction);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
        return imageInBytes;
	}
	
	public byte[] getImageInBytes(String imageUrl) throws IOException, URISyntaxException {
		URI uri = new URI(imageUrl);
        URL url = uri.toURL();
        InputStream inputStream = url.openStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        inputStream.close();
        byteArrayOutputStream.close();
		return imageBytes;
		
	}

	public String generateImageToText(String userId, String prompt, MultipartFile file) throws IOException {
		byte[] image = file.getBytes();
		File tempFile = File.createTempFile("upload", file.getOriginalFilename());
	    file.transferTo(tempFile);
        String response = ChatClient.create(chatModel).prompt()
							.user(userSpec -> userSpec.text((!StringUtils.isEmpty(prompt))?prompt:Constants.IMAGE_ANALYSIS)
					        .media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(tempFile)))
							.call().content();
        if(!StringUtils.isEmpty(response)) {
        	ImageAnalysisTransaction transaction = new ImageAnalysisTransaction();
        	transaction.setUserid(userId);
        	transaction.setQuestion(prompt);
        	transaction.setImage(image);
        	transaction.setAnswer(response);
        	dao.saveImageAnalysisGptTransaction(transaction);
        }
		return response;
	}
	
	public Map<String, List<ImageAnalysisTransaction>> getImageAnalysisTransactions(String email, String password){
		List<ImageAnalysisTransaction> imageTransactions = null;
		Map<String, List<ImageAnalysisTransaction>> mapByDate = null;
		User user = dao.getUserObject(email,password);
		if(null != user) {
			imageTransactions = dao.getImageAnalysisTransactions(email.split("@")[0]);
			if(!imageTransactions.isEmpty()) {
				mapByDate = imageTransactions.stream().collect(Collectors.groupingBy(i -> i.getDateOfChat()));
			}
		}
		return mapByDate;
	}
	
	public Map<String, List<TranslationTransaction>> getTranslationTransaction(String userId) {
		List<TranslationTransaction> translationTrans = null;
		Map<String, List<TranslationTransaction>> mapByDate = null;
		translationTrans = dao.getTranslationTransaction(userId);
		if(!translationTrans.isEmpty()) {
			mapByDate = translationTrans.stream().collect(Collectors.groupingBy(i -> i.getDateOfChat()));
		}
		return mapByDate;
	}
	
	public Flux<String> chatWithStream(String message) {
		return ChatClient.create(chatModel).prompt().user(message).stream().content();
	}
	
	public String translate(String text,  String sourceLanguage, String targetLanguage, String userId) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", Arrays.asList(
                Map.of("role", "system", "content", "Translate the following text from " + sourceLanguage + " to " + targetLanguage),
                Map.of("role", "user", "content", text)
        ));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
        	String resp = response.getBody();
        	String responseText = getCharResponseAnswer(resp);
        	TranslationTransaction trans = new TranslationTransaction();
        	trans.setUserid(userId);
        	trans.setSourceLang(sourceLanguage);
        	trans.setQuestion(text);
        	trans.setTargetLang(targetLanguage);
        	trans.setAnswer(responseText);
        	dao.saveTranslaterTransaction(trans);
            return responseText;
        } else {
            throw new RuntimeException("Error calling OpenAI API: " + response.getStatusCode());
        }
    }
	
	public String extractTextFromFile(MultipartFile file) throws IOException {
	    if (file.getOriginalFilename().endsWith(".pdf")) {
	        try (PDDocument document = PDDocument.load(file.getInputStream())) {
	            return new PDFTextStripper().getText(document);
	        }
	    } else if (file.getOriginalFilename().endsWith(".docx")) {
	        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
	            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
	            return extractor.getText();
	        }
	    } else {
	        throw new UnsupportedOperationException("File format not supported");
	    }
	}

	public String analyzeResume(MultipartFile file,String role,String userId) throws IOException {
		String resumeText = extractTextFromFile(file);
		String prompt = String.format(Constants.RESUME_ANALYSIS_PROMPT, role, resumeText);
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
        	String resp = response.getBody();
        	String responseText = getCharResponseAnswer(resp);
        	saveResumeAnalysis(userId,role,responseText,file);
            return responseText;
        } else {
            throw new RuntimeException("Error calling OpenAI API: " + response.getStatusCode());
        }
    }
	
	public void saveResumeAnalysis(String userId, String roleType, String responseText, MultipartFile file) throws IOException {
		ResumeAnalysisTransaction tran = new ResumeAnalysisTransaction();
		tran.setUserid(userId);
		tran.setRoleType(roleType);
		tran.setResumeFile(file.getBytes());
		tran.setAnswer(responseText);
		tran.setFileName(file.getOriginalFilename());
		dao.insertResumeAnalysis(tran);
	}
	
	public Map<String, List<ResumeAnalysisTransaction>> getResumeAnalysisTransactions(String email, String password){
		List<ResumeAnalysisTransaction> resumeTransactions = null;
		Map<String, List<ResumeAnalysisTransaction>> mapByDate = null;
		User user = dao.getUserObject(email,password);
		if(null != user) {
			resumeTransactions = dao.getResumeAnalysisTransactions(email.split("@")[0]);
			if(!resumeTransactions.isEmpty()) {
				mapByDate = resumeTransactions.stream().collect(Collectors.groupingBy(i -> i.getDateOfChat()));
			}
		}
		return mapByDate;
	}
	
	public Map<String, List<GifTransaction>> getGifHistory(String email, String password){
		List<GifTransaction> gifTransactions = null;
		Map<String, List<GifTransaction>> mapByDate = null;
		User user = dao.getUserObject(email,password);
		if(null != user) {
			gifTransactions = dao.getGifTransactions(email.split("@")[0]);
			if(!gifTransactions.isEmpty()) {
				mapByDate = gifTransactions.stream().collect(Collectors.groupingBy(i -> i.getDateOfChat()));
			}
		}
		return mapByDate;
	}
	
	public String saveGifTransaction(PromptRequest request, byte[] gif){
		User user = request.getUser();
    	GifTransaction gifTrans = new GifTransaction();
    	gifTrans.setUserid(user.getUserId());
    	gifTrans.setQuestion(request.getPrompt());
    	gifTrans.setAnswer(gif);
		int resp = dao.saveGifGptTransaction(gifTrans);
    	return (resp > 0)?Constants.SUCCESS_MSG:Constants.FAILURE_MSG;
    }
	
}
