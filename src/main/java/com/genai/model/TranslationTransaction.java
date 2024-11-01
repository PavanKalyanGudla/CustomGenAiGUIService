package com.genai.model;

public class TranslationTransaction {

	private int transactionId;
    private String dateOfChat;
    private String userid;
    private String question;
    private String sourceLang;
    private String answer;
    private String targetLang;
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
	public String getSourceLang() {
		return sourceLang;
	}
	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getTargetLang() {
		return targetLang;
	}
	public void setTargetLang(String targetLang) {
		this.targetLang = targetLang;
	}
	@Override
	public String toString() {
		return "TranslationTransaction [transactionId=" + transactionId + ", dateOfChat=" + dateOfChat + ", userid="
				+ userid + ", question=" + question + ", sourceLang=" + sourceLang + ", answer=" + answer
				+ ", targetLang=" + targetLang + "]";
	}
}
