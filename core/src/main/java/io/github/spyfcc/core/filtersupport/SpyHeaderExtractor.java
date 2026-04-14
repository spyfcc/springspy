package io.github.spyfcc.core.filtersupport;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class SpyHeaderExtractor {

    private SpyHeaderExtractor() {
    }

    public static Map<String, String> extract(Enumeration<String> headerNames,
                                              Function<String, String> headerValueResolver) {
        if (headerNames == null) {
            return Collections.emptyMap();
        }

        Map<String, String> headers = new LinkedHashMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, headerValueResolver.apply(headerName));
        }

        return headers;
    }
}