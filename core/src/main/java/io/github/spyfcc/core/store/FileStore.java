package io.github.spyfcc.core.store;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.spyfcc.core.event.TrafficEvent;

public class FileStore {

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

}
