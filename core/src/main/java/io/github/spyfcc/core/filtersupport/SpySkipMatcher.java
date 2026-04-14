package io.github.spyfcc.core.filtersupport;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.support.SpyPathSupport;

public final class SpySkipMatcher {

    private SpySkipMatcher() {
    }
    
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean shouldSkip(String uri, String contextPath, PropsConfig props) {
        if (uri == null || isBlank(uri)) {
            return true;
        }

        String normalizedUri = normalizeUri(uri, contextPath);
        String uiPath = SpyPathSupport.uiBasePath(props);

        if (normalizedUri.equals(uiPath) || normalizedUri.startsWith(uiPath + "/")) {
            return true;
        }

        return normalizedUri.startsWith("/error")
                || normalizedUri.startsWith("/actuator")
                || normalizedUri.endsWith(".js")
                || normalizedUri.endsWith(".css")
                || normalizedUri.endsWith(".map")
                || normalizedUri.endsWith(".png")
                || normalizedUri.endsWith(".jpg")
                || normalizedUri.endsWith(".jpeg")
                || normalizedUri.endsWith(".gif")
                || normalizedUri.endsWith(".svg")
                || normalizedUri.endsWith(".ico")
                || normalizedUri.endsWith(".woff")
                || normalizedUri.endsWith(".woff2");
    }

    private static String normalizeUri(String uri, String contextPath) {
        String result = uri.trim();

        if (contextPath != null && !isBlank(contextPath) && result.startsWith(contextPath)) {
            result = result.substring(contextPath.length());
        }

        if (isBlank(result)) {
            return "/";
        }

        return result.startsWith("/") ? result : "/" + result;
    }
}