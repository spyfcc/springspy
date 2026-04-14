package io.github.spyfcc.core.support;

import java.util.regex.Pattern;

public final class BodySanitizer {
	
	
	private BodySanitizer() {
		
	}

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "([A-Za-z0-9._%+-]{2})[A-Za-z0-9._%+-]*(@[^\"\\s]+)"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "(\"password\"\\s*:\\s*\")(.*?)(\")",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(\"(token|accessToken|refreshToken|secret|apiKey)\"\\s*:\\s*\")(.*?)(\")",
            Pattern.CASE_INSENSITIVE
    );

    public static String truncate(String body, int max) {
        if (body == null) {
            return null;
        }

        if (max <= 0) {
            return "...(truncated)";
        }

        if (body.length() <= max) {
            return body;
        }

        return body.substring(0, max) + "...(truncated)";
    }

    public static String maskSensitive(String body) {
        if (body == null) {
            return null;
        }

        String masked = body;

        masked = EMAIL_PATTERN.matcher(masked).replaceAll("$1***$2");
        masked = PASSWORD_PATTERN.matcher(masked).replaceAll("$1****$3");
        masked = TOKEN_PATTERN.matcher(masked).replaceAll("$1****$4");

        return masked;
    }

    public static String safeBody(String body, boolean maskSensitive, int maxBodySize) {
        String result = body;

        if (maskSensitive) {
            result = maskSensitive(result);
        }

        return truncate(result, maxBodySize);
    }

}
