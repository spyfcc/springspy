package io.github.spyfcc.starter3.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.filtersupport.SpyAuthPathSupport;
import io.github.spyfcc.core.support.SpyPathSupport;
import io.github.spyfcc.core.support.SpySessionSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SpyAuthFilter extends OncePerRequestFilter {

	private final PropsConfig props;

	public SpyAuthFilter(PropsConfig props) {
		this.props = props;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String basePath = SpyPathSupport.uiBasePath(props);
		String path = SpyAuthPathSupport.normalizePath(request.getRequestURI(), request.getContextPath());

		if (SpyAuthPathSupport.isProtectedSpyPath(path, basePath)) {

			if (SpyAuthPathSupport.isAllowedWithoutAuth(path, basePath)) {
				filterChain.doFilter(request, response);
				return;
			}

			Object user = request.getSession(false) != null
					? request.getSession(false).getAttribute(SpySessionSupport.SESSION_USER)
					: null;

			if (!SpySessionSupport.isLoggedIn(user)) {
				response.sendRedirect(SpyAuthPathSupport.loginRedirectPath(request.getContextPath(), basePath));
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}