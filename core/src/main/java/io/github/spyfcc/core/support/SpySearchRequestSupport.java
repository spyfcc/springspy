package io.github.spyfcc.core.support;

import io.github.spyfcc.core.dto.SpySearchRequest;

public final class SpySearchRequestSupport {

	private SpySearchRequestSupport() {
	}

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    } 
	public static void normalize(SpySearchRequest request) {
		if (request.getPage() == null || request.getPage() < 0) {
			request.setPage(0);
		}

		if (request.getSize() == null || request.getSize() <= 0) {
			request.setSize(20);
		}
	}

	public static boolean hasCriteria(SpySearchRequest request) {
		if (request == null) {
			return false;
		}

		return hasText(request.getMethod()) || request.getStatus() != null || hasText(request.getUri())
				|| hasText(request.getText()) || hasText(request.getRequestBody()) || hasText(request.getResponseBody())
				|| request.getFromDate() != null || request.getToDate() != null;
	}

	private static boolean hasText(String value) {
		return value != null && ! isBlank(value);
	}
}