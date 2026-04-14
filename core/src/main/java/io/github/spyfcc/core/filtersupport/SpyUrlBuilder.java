package io.github.spyfcc.core.filtersupport;

public final class SpyUrlBuilder {

    private SpyUrlBuilder() {
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    public static String buildFullUrl(String requestUrl, String queryParams) {
        if (requestUrl == null) {
            return "";
        }

        if (queryParams == null || isBlank(queryParams)) {
            return requestUrl;
        }

        return requestUrl + "?" + queryParams;
    }
}