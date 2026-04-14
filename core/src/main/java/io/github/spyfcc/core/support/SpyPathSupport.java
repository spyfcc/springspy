package io.github.spyfcc.core.support;

import io.github.spyfcc.core.config.PropsConfig;

public final class SpyPathSupport {

	private SpyPathSupport() {
	}

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
	public static String uiBasePath(PropsConfig props) {
		String path = props.getUiPath();
		if (path == null || isBlank(path)) {
			return "/spy";
		}

		path = path.trim();

		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		if (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

	public static String fullUiPath(String contextPath, PropsConfig props) {
		String ctx = contextPath == null ? "" : contextPath.trim();
		return ctx + uiBasePath(props);
	}

}
