package io.github.spyfcc.core.support;

public final class SpySessionSupport {

    private SpySessionSupport() {
    }

    public static final String SESSION_USER = "SPY_SESSION_USER";

    public static boolean isLoggedIn(Object sessionUser) {
        return sessionUser != null;
    }
}