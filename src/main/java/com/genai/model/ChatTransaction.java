package com.genai.model;

public class ChatTransaction {
	private int transactionId;
    private String dateOfChat;
    private String userid;
    private String question;
    private String answer;
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public String getDateOfChat() {
		return dateOfChat;
	}
	public void setDateOfChat(String dateOfChat) {
		this.dateOfChat = dateOfChat;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	@Override
	public String toString() {
		return "ChatTransaction [transactionId=" + transactionId + ", dateOfChat=" + dateOfChat + ", userid=" + userid
				+ ", question=" + question + ", answer=" + answer + "]";
	}
}
