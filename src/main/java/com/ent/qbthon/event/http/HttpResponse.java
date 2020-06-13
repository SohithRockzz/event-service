package com.ent.qbthon.event.http;

public class HttpResponse {
	private final String statusCode;
	private final String statusMessage;
	private final String responseDesc;

	private final Object responseData;

	public HttpResponse(String statusCode, String statusMessage, String responseDesc, Object responseData) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.responseDesc = responseDesc;
		this.responseData = responseData;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public Object getResponseData() {
		return responseData;
	}

}
