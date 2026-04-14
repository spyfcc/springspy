package io.github.spyfcc.core.filtersupport;

import java.util.Map;
import java.util.stream.Collectors;

public final class SpyRequestPayloadExtractor {

	private SpyRequestPayloadExtractor() {
	}

	public static SpyRequestPayload extract(String contentType, Map<String, String[]> parameterMap,
			String rawRequestBody) {

		if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
			String formParams = parameterMap.entrySet().stream()
					.map(e -> e.getKey() + "=" + String.join(",", e.getValue())).collect(Collectors.joining("&"));

			return new SpyRequestPayload(formParams, "");
		}

		return new SpyRequestPayload("", rawRequestBody != null ? rawRequestBody : "");
	}
}