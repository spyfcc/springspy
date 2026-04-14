package io.github.spyfcc.starter2.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.manager.StorageManager;
import io.github.spyfcc.core.support.SpySessionSupport;
import io.github.spyfcc.core.support.SpyUiRouteSupport;
import io.github.spyfcc.core.support.SpyViewModelSupport;

@Controller
@RequestMapping("${traffic.spy.ui-path:/spy}")
public class SpyUIScreenController {

	   private final PropsConfig props;
	    private final StorageManager storageManager;

	    public SpyUIScreenController(PropsConfig props, StorageManager storageManager) {
	        this.props = props;
	        this.storageManager = storageManager;
	    }


	    @GetMapping
	    public String index(HttpSession session) {
	        Object user = session.getAttribute(SpySessionSupport.SESSION_USER);
	        if (SpySessionSupport.isLoggedIn(user)) {
	            return SpyUiRouteSupport.redirectToLogs(props);
	        }
	        return SpyUiRouteSupport.redirectToLogin(props);
	    }
	    
	    @GetMapping("/logs")
	    public String showUI(HttpServletRequest request, HttpSession session, Model model) {
	        Object user = session.getAttribute(SpySessionSupport.SESSION_USER);
	        if (!SpySessionSupport.isLoggedIn(user)) {
	            return SpyUiRouteSupport.redirectToLogin(props);
	        }

	        model.addAllAttributes(
	                SpyViewModelSupport.pageModel(request.getContextPath(), props, "Spy Traffic Dashboard")
	        );
	        model.addAttribute("contentPage", "spy/pages/logs");
	        model.addAttribute("events", storageManager.memory().list());

	        return "spy/layout/base";
	    }

	    @GetMapping("/login")
	    public String loginPage(HttpServletRequest request, Model model) {
	        model.addAllAttributes(
	                SpyViewModelSupport.baseModel(request.getContextPath(), props)
	        );
	        return "spy/pages/login";
	    }

	    @PostMapping("/login")
	    public String handleLogin(@RequestParam("username") String username,
	                              @RequestParam("password") String password,
	                              HttpSession session) {

	        if (props.getSecurity().getUsername().equals(username)
	                && props.getSecurity().getPassword().equals(password)) {
	            session.setAttribute(SpySessionSupport.SESSION_USER, username);
	            return SpyUiRouteSupport.redirectToLogs(props);
	        }

	        return SpyUiRouteSupport.redirectToLoginError(props);
	    }

	    @PostMapping("/logout")
	    public String logout(HttpSession session) {
	        session.invalidate();
	        return SpyUiRouteSupport.redirectToLoginLogout(props);
	    }

	    @GetMapping("/logs/fragment")
	    public String logsFragment(HttpSession session, Model model) {
	        Object user = session != null
	                ? session.getAttribute(SpySessionSupport.SESSION_USER)
	                : null;

	        if (!SpySessionSupport.isLoggedIn(user)) {
	            return SpyUiRouteSupport.redirectToLogin(props);
	        }
	        
	        List<TrafficEvent> events = new ArrayList<>(storageManager.memory().list());
	        Collections.reverse(events);

	        model.addAttribute("events", events);
	        return "spy/fragments/logs-table :: table";
	    }
}