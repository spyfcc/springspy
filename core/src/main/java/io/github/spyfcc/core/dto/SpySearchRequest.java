package io.github.spyfcc.core.dto;

public class SpySearchRequest {

	private String fromDate;
	private String toDate;
	private String method;
	private Integer status;
	private String uri;
	private String requestBody;
	private String responseBody;
	private String text;
	private Integer page = 0;
	private Integer size = 20;

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	public boolean isEmpty() {
		return isBlank(fromDate)
				&& isBlank(toDate)
				&& isBlank(method)
				&& status == null
				&& isBlank(uri)
				&& isBlank(requestBody)
				&& isBlank(responseBody)
				&& isBlank(text);
	}
	
	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

}
