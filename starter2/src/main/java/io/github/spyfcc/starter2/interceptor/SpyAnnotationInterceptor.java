package io.github.spyfcc.starter2.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.spyfcc.core.annotation.NoSpy;
import io.github.spyfcc.core.support.SpyRequestAttributesSupport;



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
