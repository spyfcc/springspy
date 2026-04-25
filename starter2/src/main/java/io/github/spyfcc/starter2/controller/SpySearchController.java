package io.github.spyfcc.starter2.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.store.SpyStore;
import io.github.spyfcc.core.support.SpySearchRequestSupport;
import io.github.spyfcc.core.support.SpySessionSupport;
import io.github.spyfcc.core.support.SpyUiRouteSupport;
import io.github.spyfcc.core.support.SpyViewModelSupport;

@Controller
@RequestMapping("${traffic.spy.ui-path:/spy}")
public class SpySearchController {

    private final SpyStore spyStore;
    private final PropsConfig props;

    public SpySearchController(SpyStore spyStore, PropsConfig props) {
        this.spyStore = spyStore;
        this.props = props;
    }

    @GetMapping("/search")
    public String searchPage(HttpServletRequest request,
                             HttpSession session,
                             Model model,
                             @ModelAttribute SpySearchRequest searchRequest) {

        Object user = session.getAttribute(SpySessionSupport.SESSION_USER);
        if (!SpySessionSupport.isLoggedIn(user)) {
            return SpyUiRouteSupport.redirectToLogin(props);
        }

        SpySearchRequestSupport.normalize(searchRequest);

        model.addAllAttributes(
                SpyViewModelSupport.pageModel(request.getContextPath(), props, "Spy Search")
        );
        model.addAttribute("searchRequest", searchRequest);

        if (SpySearchRequestSupport.hasCriteria(searchRequest)) {
            SpySearchResult result = spyStore.search(searchRequest);
            model.addAttribute("result", result);
        }

        model.addAttribute("contentPage", "spy/pages/search");
        return "spy/layout/base";
    }
}