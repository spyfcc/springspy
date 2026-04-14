package io.github.spyfcc.starter3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.search.SpySearchService;
import io.github.spyfcc.core.support.SpySearchRequestSupport;
import io.github.spyfcc.core.support.SpySessionSupport;
import io.github.spyfcc.core.support.SpyUiRouteSupport;
import io.github.spyfcc.core.support.SpyViewModelSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("${traffic.spy.ui-path:/spy}")
public class SpySearchController {

	private final SpySearchService searchService;
	private final PropsConfig props;
	public SpySearchController(SpySearchService searchService, PropsConfig props) {
		this.searchService = searchService;
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
	        SpySearchResult result = searchService.search(searchRequest);
	        model.addAttribute("result", result);
	    }

	    model.addAttribute("contentPage", "spy/pages/search");
	    return "spy/layout/base";
	}

}
