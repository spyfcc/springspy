package io.github.spyfcc.core.filtersupport;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.support.BodySanitizer;

public final class SpyTrafficEventFactory {

	private SpyTrafficEventFactory() {
	}

	public static TrafficEvent create(String method, String uri, String fullUrl, int status, long duration,
			String queryParams, String formParams, String requestBody, String responseBody, String contentType,
			Map<String, String> headers, String clientIp, int maxBodySize) {

		TrafficEvent event = TrafficEvent.create();
		event.setTimestamp(LocalDateTime.now());
		event.setMethod(method);
		event.setUri(uri);
		event.setFullUrl(fullUrl);
		event.setStatus(status);
		event.setDuration(duration);
		event.setQueryParams(queryParams);
		event.setFormParams(BodySanitizer.truncate(formParams, maxBodySize));
		event.setRequestBody(BodySanitizer.truncate(requestBody, maxBodySize));
		event.setResponseBody(BodySanitizer.truncate(responseBody, maxBodySize));
		event.setContentType(contentType);
		event.setHeaders(headers);
		event.setClientIp(clientIp);

		return event;
	}

}
