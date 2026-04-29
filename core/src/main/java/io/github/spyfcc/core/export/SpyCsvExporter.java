package io.github.spyfcc.core.export;

import io.github.spyfcc.core.event.TrafficEvent;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SpyCsvExporter {

    private SpyCsvExporter() {}

    public static byte[] export(List<TrafficEvent> events) {
        StringBuilder csv = new StringBuilder();
        csv.append("Timestamp,Method,URI,Status,Duration(ms),ClientIp,Username,RequestBody,ResponseBody\n");

        for (TrafficEvent e : events) {
            csv.append(escape(String.valueOf(e.getTimestamp()))).append(",")
                    .append(escape(e.getMethod())).append(",")
                    .append(escape(e.getUri())).append(",")
                    .append(escape(String.valueOf(e.getStatus()))).append(",")
                    .append(escape(String.valueOf(e.getDuration()))).append(",")
                    .append(escape(e.getClientIp())).append(",")
                    .append(escape(e.getUsername())).append(",")
                    .append(escape(e.getRequestBody())).append(",")
                    .append(escape(e.getResponseBody())).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}