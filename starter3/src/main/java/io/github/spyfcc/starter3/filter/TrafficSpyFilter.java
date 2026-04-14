package io.github.spyfcc.starter3.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.filtersupport.SpyClientIpResolver;
import io.github.spyfcc.core.filtersupport.SpyHeaderExtractor;
import io.github.spyfcc.core.filtersupport.SpyRequestPayload;
import io.github.spyfcc.core.filtersupport.SpyRequestPayloadExtractor;
import io.github.spyfcc.core.filtersupport.SpySkipMatcher;
import io.github.spyfcc.core.filtersupport.SpyTrafficEventFactory;
import io.github.spyfcc.core.filtersupport.SpyUrlBuilder;
import io.github.spyfcc.core.manager.StorageManager;
import io.github.spyfcc.core.support.BodySanitizer;
import io.github.spyfcc.core.support.SpyRequestAttributesSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TrafficSpyFilter extends OncePerRequestFilter {
	private final StorageManager storageManager;
	private final int maxBodySize;
	private final boolean maskSensitive;
	private final PropsConfig props;

	@Autowired
	private ApplicationContext applicationContext;

	public TrafficSpyFilter(StorageManager storageManager, int maxBodySize, boolean maskSensitive, PropsConfig props) {
		this.storageManager = storageManager;
		this.maxBodySize = maxBodySize;
		this.maskSensitive = maskSensitive;
		this.props = props;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
	        throws ServletException, IOException {

	    if (SpySkipMatcher.shouldSkip(req.getRequestURI(), req.getContextPath(), props)) {
	        chain.doFilter(req, res);
	        return;
	    }

	    ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(req);
	    ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(res);

	    long start = System.currentTimeMillis();

	    String queryParams = request.getQueryString();
	    String fullUrl = SpyUrlBuilder.buildFullUrl(request.getRequestURL().toString(), queryParams);
	    String contentType = request.getContentType();

	    Map<String, String> headers = SpyHeaderExtractor.extract(request.getHeaderNames(), request::getHeader);

	    Exception capturedException = null;

	    try {
	        chain.doFilter(request, response);
	    } catch (Exception ex) {
	        capturedException = ex;
	        throw ex;
	    } finally {
	        try {
	            if (applicationContext instanceof ConfigurableApplicationContext
	                    && !((ConfigurableApplicationContext) applicationContext).isActive()) {
	                return;
	            }

	            if (request.getAttribute(SpyRequestAttributesSupport.SKIP_SPY_LOGGING) != null) {
	                return;
	            }

	            long duration = System.currentTimeMillis() - start;

	            String rawRequestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
	            String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

	            SpyRequestPayload payload = SpyRequestPayloadExtractor.extract(
	                    contentType,
	                    request.getParameterMap(),
	                    rawRequestBody
	            );

	            String formParams = payload.getFormParams();
	            String requestBody = payload.getRequestBody();

	            int status = response.getStatus();
	            if (capturedException != null && status == HttpServletResponse.SC_OK) {
	                status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	            }

	            if (capturedException != null) {
	                String errorMessage = capturedException.getClass().getSimpleName()
	                        + ": "
	                        + capturedException.getMessage();

	                if (responseBody == null || responseBody.trim().isEmpty()) {
	                    responseBody = errorMessage;
	                }
	            }

	            if (maskSensitive) {
	                responseBody = BodySanitizer.maskSensitive(responseBody);
	                formParams = BodySanitizer.maskSensitive(formParams);
	                requestBody = BodySanitizer.maskSensitive(requestBody);
	            }

	            String clientIp = SpyClientIpResolver.resolve(
	                    req.getHeader("X-Forwarded-For"),
	                    req.getHeader("X-Real-IP"),
	                    req.getHeader("Proxy-Client-IP"),
	                    req.getHeader("WL-Proxy-Client-IP"),
	                    req.getRemoteAddr()
	            );

	            TrafficEvent event = SpyTrafficEventFactory.create(
	                    req.getMethod(),
	                    req.getRequestURI(),
	                    fullUrl,
	                    status,
	                    duration,
	                    queryParams,
	                    formParams,
	                    requestBody,
	                    responseBody,
	                    contentType,
	                    headers,
	                    clientIp,
	                    maxBodySize
	            );

	            storageManager.storeAsync(event);
	        } finally {
	            response.copyBodyToResponse();
	        }
	    }
	}
}
