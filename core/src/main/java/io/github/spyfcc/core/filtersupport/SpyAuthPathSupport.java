package io.github.spyfcc.core.filtersupport;

public final class SpyAuthPathSupport {

    private SpyAuthPathSupport() {
    }
    
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String normalizePath(String requestUri, String contextPath) {
        if (requestUri == null || isBlank(requestUri)) {
            return "/";
        }

        String path = requestUri;

        if (contextPath != null && !isBlank(contextPath) && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        if (isBlank(path)) {
            return "/";
        }

        return path.startsWith("/") ? path : "/" + path;
    }

    public static boolean isProtectedSpyPath(String path, String basePath) {
        if (path == null || basePath == null) {
            return false;
        }

        return path.equals(basePath) || path.startsWith(basePath + "/");
    }

    public static boolean isAllowedWithoutAuth(String path, String basePath) {
        if (path == null || basePath == null) {
            return false;
        }

        return path.equals(basePath)
                || path.equals(basePath + "/login")
                || path.equals(basePath + "/logout")
                || path.startsWith(basePath + "/static/");
    }

    public static String loginRedirectPath(String contextPath, String basePath) {
        String ctx = (contextPath == null) ? "" : contextPath;
        return ctx + basePath + "/login";
    }
}