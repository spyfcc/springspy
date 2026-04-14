package io.github.spyfcc.core.support;

import io.github.spyfcc.core.config.PropsConfig;

public final class SpyUiRouteSupport {

    private SpyUiRouteSupport() {
    }

    public static String login(PropsConfig props) {
        return SpyPathSupport.uiBasePath(props) + "/login";
    }

    public static String loginError(PropsConfig props) {
        return SpyPathSupport.uiBasePath(props) + "/login?error=true";
    }

    public static String loginLogout(PropsConfig props) {
        return SpyPathSupport.uiBasePath(props) + "/login?logout=true";
    }

    public static String logs(PropsConfig props) {
        return SpyPathSupport.uiBasePath(props) + "/logs";
    }

    public static String redirectToLogin(PropsConfig props) {
        return "redirect:" + login(props);
    }

    public static String redirectToLoginError(PropsConfig props) {
        return "redirect:" + loginError(props);
    }

    public static String redirectToLoginLogout(PropsConfig props) {
        return "redirect:" + loginLogout(props);
    }

    public static String redirectToLogs(PropsConfig props) {
        return "redirect:" + logs(props);
    }
}