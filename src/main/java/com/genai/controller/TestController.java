package com.genai.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.model.GifTransaction;
import com.genai.model.PromptRequest;
import com.genai.service.GenAiService;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

@RestController
@CrossOrigin("http://localhost:4200")
public class TestController {
		
	@Autowired
	private GenAiService genAiService;
	
	@Value("${spring.ai.openai.api-key}")
	private String apiKey;
	
	public final static int imageCount = 2; 
	
    private static final String IMAGE_GENERATION_API_URL = "https://api.openai.com/v1/images/generations";
    
    private static List<String> imageSize = Arrays.asList(
    		"512x512",
    		"256x256"
    );
    
    @PostMapping("/generateGif")
    public ResponseEntity<byte[]> generateGifFromPrompt(@RequestBody PromptRequest request) {
        try {
        	List<BufferedImage> images = generateImages(request);
            byte[] gif = createGif(images, request.getFrameDelay());
            genAiService.saveGifTransaction(request,gif);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_GIF);
            headers.setContentLength(gif.length);
            return new ResponseEntity<>(gif, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private List<BufferedImage> generateImages(PromptRequest request) throws Exception {
        List<BufferedImage> images = new ArrayList<>();
        BufferedImage read  = null;
        String imageUrl = "";
        for (int i = 0; i < imageCount; i++) {
        	if(i==0) {
        		imageUrl = extractImageUrlFromResponse(callImageGenerationApi(imageUrl+" "+request.getPrompt(),imageSize.get(1)));
        	}else {
        		imageUrl = extractImageUrlFromResponse(generateEditImage(request.getPrompt(),imageUrl));
        	}
            read = fetchImage(imageUrl);
            images.add(read);
        }
        return images;
    }
        
    private static BufferedImage fetchImage(String imageUrl) throws IOException {
        URI uri = URI.create(imageUrl);
        URL url = uri.toURL();
        return ImageIO.read(url);
    }

    private String callImageGenerationApi(String prompt,String size) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(IMAGE_GENERATION_API_URL);
            request.setHeader("Authorization", "Bearer "+apiKey);
            request.setHeader("Content-Type", "application/json");
            String jsonPayload = String.format("{\"prompt\": \"%s\", \"n\": 1, \"size\": \"%s\"}", prompt,size);
            request.setEntity(new StringEntity(jsonPayload));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return new String(response.getEntity().getContent().readAllBytes());
            }
        }
    }

    private String extractImageUrlFromResponse(String response) {
        String answer = "";
        ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(response);
			answer = node.get("data").get(0).get("url").asText();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return answer;
    }
    
    private byte[] createGif(List<BufferedImage> images, int frameDelay) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(outputStream);
        gifEncoder.setDelay(frameDelay);
        gifEncoder.setRepeat(0);
        for (BufferedImage image : images) {
            gifEncoder.addFrame(image);
        }
        gifEncoder.finish();
        return outputStream.toByteArray();
    }
    
    public String generateEditImage(String prompt,String imageUrl) {
        try {
            ByteArrayResource imageResource = getImageAsByteArrayResource(imageUrl);
            return generateImageWithChanges(prompt, imageResource.getByteArray(),loadMaskFromUrl(imageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating image: " + e.getMessage();
        }
    }
    
    public ByteArrayResource getImageAsByteArrayResource(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        try (InputStream in = url.openStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return new ByteArrayResource(out.toByteArray());
        }
    }

    public byte[] loadMaskFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
    
    public String generateImageWithChanges(String prompt, byte[] imageBytes1, byte[] maskBytes1) throws Exception {
    	byte[] imageBytes = convertToRGBA(imageBytes1);
    	byte[] maskBytes = convertToRGBA(maskBytes1);
        String url = "https://api.openai.com/v1/images/edits";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "multipart/form-data");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("prompt", prompt);
        body.add("image", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        });
        body.add("mask", new ByteArrayResource(maskBytes) {
            @Override
            public String getFilename() {
                return "mask.png";
            }
        });
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to edit the image.";
        }
    }

    public byte[] convertToRGBA(byte[] inputImageBytes) throws Exception {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(inputImageBytes));
        BufferedImage rgbaImage = new BufferedImage(
            originalImage.getWidth(),
            originalImage.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );
        rgbaImage.getGraphics().drawImage(originalImage, 0, 0, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(rgbaImage, "png", outputStream);
        return outputStream.toByteArray();
    }
}