package io.github.spyfcc.starter3.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.manager.StorageManager;
import io.github.spyfcc.core.support.SpySessionSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("${traffic.spy.ui-path:/spy}")
public class SpyLogsRestController {

	private final StorageManager storageManager;
	public SpyLogsRestController(StorageManager storageManager) {
		this.storageManager = storageManager;
	}
	
    @GetMapping("/api/logs")
    public List<TrafficEvent> getAllLogs(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object user = session != null
                ? session.getAttribute(SpySessionSupport.SESSION_USER)
                : null;

        if (!SpySessionSupport.isLoggedIn(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        List<TrafficEvent> events = new ArrayList<>(storageManager.memory().list());
        Collections.reverse(events);
        return events;
    }
}
