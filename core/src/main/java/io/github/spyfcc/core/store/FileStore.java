package io.github.spyfcc.core.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.event.TrafficEvent;

public class FileStore implements SpyStore {

	private final File baseDir;
	private final ObjectMapper mapper;
	

	public FileStore(String basePath) {
		this.baseDir = new File(basePath);
		if (!this.baseDir.exists()) {
			this.baseDir.mkdirs();
		}
		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new JavaTimeModule());
		this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	
	}

	private File getCurrentLogFile() {
		String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
		return new File(baseDir, "sp-" + date + ".log");
	}

	@Override
	public synchronized void save(TrafficEvent event) {
		try {
			File logFile = getCurrentLogFile();
			try (java.io.FileOutputStream fos = new java.io.FileOutputStream(logFile, true);
					java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(fos,
							java.nio.charset.StandardCharsets.UTF_8)) {
				String jsonLine = mapper.writeValueAsString(event);
				osw.write(jsonLine);
				osw.write(System.lineSeparator());
				osw.flush();
			}
		} catch (Exception e) {
			System.err.println("Traffic Spy FileStore Error : " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public SpySearchResult search(SpySearchRequest request) {
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
	
	
    private List<Path> resolveFiles(SpySearchRequest request) {
        LocalDate from = parseDate(request.getFromDate(), LocalDate.now().minusDays(1));
        LocalDate to = parseDate(request.getToDate(), LocalDate.now());

        if (from.isAfter(to)) {
            LocalDate tmp = from;
            from = to;
            to = tmp;
        }

        Path logDir = baseDir.toPath();
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
    
    private void readFile(Path file, SpySearchRequest request, List<TrafficEvent> result) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (isBlank(line)) {
                    continue;
                }

                try {
                    TrafficEvent event = mapper.readValue(line, TrafficEvent.class);
                    if (matches(event, request)) {
                        result.add(event);
                    }
                } catch (Exception ignore) {

                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Log file could not be read: " + file, e);
        }
    }
    
    private LocalDate parseDate(String value, LocalDate defaultVal) {
        if (value == null || isBlank(value)) {
            return defaultVal;
        }
        return LocalDate.parse(value);
    }
    
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
    
    private boolean contains(String source, String search) {
        if (search == null || isBlank(search)) {
            return true;
        }
        return source != null && source.toLowerCase().contains(search.toLowerCase());
    }
    
    private String safe(String s) {
        return s == null ? "" : s;
    }

}
