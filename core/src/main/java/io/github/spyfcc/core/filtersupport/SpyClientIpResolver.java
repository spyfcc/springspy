package io.github.spyfcc.core.filtersupport;

public final class SpyClientIpResolver {

	private SpyClientIpResolver() {
	}

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
	public static String resolve(String xForwardedFor, String xRealIp, String proxyClientIp, String wlProxyClientIp,
			String remoteAddr) {

		String ip = firstValid(xForwardedFor);

		if (ip == null)
			ip = firstValid(xRealIp);
		if (ip == null)
			ip = firstValid(proxyClientIp);
		if (ip == null)
			ip = firstValid(wlProxyClientIp);
		if (ip == null)
			ip = remoteAddr;

		return extractFirst(ip);
	}

	private static String firstValid(String value) {
		if (value == null || isBlank(value))
			return null;
		if ("unknown".equalsIgnoreCase(value))
			return null;
		return value.trim();
	}

	private static String extractFirst(String ip) {
		if (ip == null)
			return null;

		int commaIndex = ip.indexOf(',');
		return commaIndex > 0 ? ip.substring(0, commaIndex).trim() : ip.trim();
	}
}