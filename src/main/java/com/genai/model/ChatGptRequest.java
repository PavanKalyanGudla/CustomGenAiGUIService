package com.genai.model;

public class ChatGptRequest {
	private User user;
	private String prompt;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	@Override
	public String toString() {
		return "ChatGptRequest [user=" + user + ", prompt=" + prompt + "]";
	}
}
