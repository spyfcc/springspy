package io.github.spyfcc.core.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.spyfcc.core.config.PropsConfig;
import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.event.TrafficEvent;

public class SpyFileSearchService implements SpySearchService {

    private final PropsConfig props;
    private final ObjectMapper objectMapper;
    
    public SpyFileSearchService(PropsConfig props) {
        this.props = props;
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }
    
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }    

    @Override
    public SpySearchResult search(SpySearchRequest request) {
        return doSearch(request);
    }

    private LocalDate parseDate(String value, LocalDate defaultVal) {
        if (value == null || isBlank(value)) {
            return defaultVal;
        }
        return LocalDate.parse(value);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private boolean contains(String source, String search) {
        if (search == null || isBlank(search)) {
            return true;
        }
        return source != null && source.toLowerCase().contains(search.toLowerCase());
    }

    private boolean matches(TrafficEvent event, SpySearchRequest req) {
        if (event == null) {
            return false;
        }

        if (req.getMethod() != null && ! isBlank(req.getMethod())) {
            if (event.getMethod() == null || !event.getMethod().equalsIgnoreCase(req.getMethod())) {
                return false;
            }
        }

        if (req.getStatus() != null) {
            if (event.getStatus() == null || !req.getStatus().equals(event.getStatus())) {
                return false;
            }
        }

        if (!contains(event.getUri(), req.getUri())) {
            return false;
        }

        if (!contains(event.getRequestBody(), req.getRequestBody())) {
            return false;
        }

        if (!contains(event.getResponseBody(), req.getResponseBody())) {
            return false;
        }

        if (req.getText() != null && !isBlank(req.getText())) {
            String all = String.join(" ",
                    safe(event.getUri()),
                    safe(event.getMethod()),
                    safe(event.getRequestBody()),
                    safe(event.getResponseBody()),
                    safe(String.valueOf(event.getStatus())));

            if (!all.toLowerCase().contains(req.getText().toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    private void readFile(Path file, SpySearchRequest request, List<TrafficEvent> result) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (isBlank(line)) {
                    continue;
                }

                try {
                    TrafficEvent event = objectMapper.readValue(line, TrafficEvent.class);
                    if (matches(event, request)) {
                        result.add(event);
                    }
                } catch (Exception ignore) {
                    // tek satır bozuksa tüm aramayı patlatma
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Log file could not be read: " + file, e);
        }
    }

    private List<Path> resolveFiles(SpySearchRequest request) {
        LocalDate from = parseDate(request.getFromDate(), LocalDate.now().minusDays(1));
        LocalDate to = parseDate(request.getToDate(), LocalDate.now());

        if (from.isAfter(to)) {
            LocalDate tmp = from;
            from = to;
            to = tmp;
        }

        Path logDir = Paths.get(props.getFilePath());
        List<Path> files = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            Path file = logDir.resolve("sp-" + date.format(formatter) + ".log");
            if (Files.exists(file)) {
                files.add(file);
            }
        }

        return files;
    }

    private SpySearchResult doSearch(SpySearchRequest request) {
        List<Path> files = resolveFiles(request);
        List<TrafficEvent> matches = new ArrayList<>();

        for (Path file : files) {
            readFile(file, request, matches);
        }

        matches.sort(Comparator.comparing(TrafficEvent::getTimestamp,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        int page = request.getPage() == null || request.getPage() < 0 ? 0 : request.getPage();
        int size = request.getSize() <= 0 ? 20 : request.getSize();

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, matches.size());

        List<TrafficEvent> pageContent =
                fromIndex >= matches.size() ? Collections.emptyList() : matches.subList(fromIndex, toIndex);

        SpySearchResult result = new SpySearchResult();
        result.setContent(pageContent);
        result.setTotalElements(matches.size());
        result.setPage(page);
        result.setSize(size);
        result.setHasNext(toIndex < matches.size());

        return result;
    }
}