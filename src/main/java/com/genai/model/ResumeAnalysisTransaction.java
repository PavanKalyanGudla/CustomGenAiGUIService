package com.genai.model;

public class ResumeAnalysisTransaction {
	
	private int transactionId;
    private String dateOfChat;
	private String userid;
    private String roleType;
    private byte[] resumeFile;
    private String answer;
    private String fileName;
    
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
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public byte[] getResumeFile() {
		return resumeFile;
	}
	public void setResumeFile(byte[] resumeFile) {
		this.resumeFile = resumeFile;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
}
