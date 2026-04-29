package io.github.spyfcc.starter2.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.export.SpyCsvExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.util.List;

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

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(HttpServletRequest request,
                                            HttpSession session,
                                            @ModelAttribute SpySearchRequest searchRequest) {
        Object user = session.getAttribute(SpySessionSupport.SESSION_USER);
        if (!SpySessionSupport.isLoggedIn(user)) {
            return ResponseEntity.status(401).build();
        }

        SpySearchRequestSupport.normalize(searchRequest);
        List<TrafficEvent> events = spyStore.search(searchRequest).getContent();
        byte[] bytes = SpyCsvExporter.export(events);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=spy-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}