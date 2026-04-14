package io.github.spyfcc.starter3.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.spyfcc.core.annotation.NoSpy;
import io.github.spyfcc.core.support.SpyRequestAttributesSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SpyAnnotationInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			boolean noSpy = hm.getMethod().isAnnotationPresent(NoSpy.class)
					|| hm.getBeanType().isAnnotationPresent(NoSpy.class);
			if (noSpy) {
				request.setAttribute(SpyRequestAttributesSupport.SKIP_SPY_LOGGING, true);
			}
		}
		return true;
	}
}
