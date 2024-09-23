package com.genai.model;

public class ResponseObj {
	
	private String responseMsg;
	private String responseCode;
	private Object responseModel;
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public Object getResponseModel() {
		return responseModel;
	}
	public void setResponseModel(Object responseModel) {
		this.responseModel = responseModel;
	}

}
