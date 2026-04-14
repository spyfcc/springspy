package io.github.spyfcc.core.filtersupport;

public final class SpyRequestPayload {

	private final String formParams;
	private final String requestBody;

	public SpyRequestPayload(String formParams, String requestBody) {
		this.formParams = formParams;
		this.requestBody = requestBody;
	}

	public String getFormParams() {
		return formParams;
	}

	public String getRequestBody() {
		return requestBody;
	}
}