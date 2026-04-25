package io.github.spyfcc.core.store;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.event.TrafficEvent;

public class JdbcStore implements SpyStore {

    private static final String TABLE_NAME = "spy_traffic_log";

    private final DataSource dataSource;
    private final ObjectMapper mapper;

    public JdbcStore(DataSource dataSource) {
        this.dataSource = dataSource;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection con = dataSource.getConnection()) {
            if (tableExists(con, TABLE_NAME)) {
                return;
            }

            String sql =
            	    "CREATE TABLE " + TABLE_NAME + " (" +
            	        "id VARCHAR(36) NOT NULL PRIMARY KEY, " +
            	        "created_at TIMESTAMP NOT NULL, " +
            	        "event_time TIMESTAMP, " +
            	        "method VARCHAR(20), " +
            	        "uri VARCHAR(1000), " +
            	        "status INTEGER, " +
            	        "duration_ms BIGINT, " +
            	        "content_type VARCHAR(255), " +
            	        "client_ip VARCHAR(100), " +
            	        "username VARCHAR(255), " +
            	        "request_body VARCHAR(4000), " +
            	        "response_body VARCHAR(4000), " +
            	        "event_json VARCHAR(8000) NOT NULL" +
            	    ")";

            try (Statement st = con.createStatement()) {
                st.execute(sql);
            }

        } catch (Exception e) {
            System.err.println("Traffic Spy JdbcStore create table error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean tableExists(Connection con, String tableName) throws Exception {
        DatabaseMetaData metaData = con.getMetaData();

        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            if (rs.next()) {
                return true;
            }
        }

        try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next();
        }
    }

    @Override
    public void save(TrafficEvent event) {
        String id = event.getId();

        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
            event.setId(id);
        }

        String sql =
                "INSERT INTO " + TABLE_NAME + " " +
                        "(id, created_at, event_time, method, uri, status, duration_ms, content_type, client_ip, username, request_body, response_body, event_json) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            setTimestamp(ps, 3, event.getTimestamp());

            ps.setString(4, event.getMethod());
            ps.setString(5, event.getUri());

            if (event.getStatus() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, event.getStatus());
            }

            if (event.getDuration() == null) {
                ps.setNull(7, Types.BIGINT);
            } else {
                ps.setLong(7, event.getDuration());
            }

            ps.setString(8, event.getContentType());
            ps.setString(9, event.getClientIp());
            ps.setString(10, event.getUsername());
            ps.setString(11, event.getRequestBody());
            ps.setString(12, event.getResponseBody());
            ps.setString(13, mapper.writeValueAsString(event));

            ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Traffic Spy JdbcStore save error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public SpySearchResult search(SpySearchRequest request) {
        List<Object> params = new ArrayList<Object>();
        String where = buildWhere(request, params);

        int page = request.getPage() == null || request.getPage() < 0 ? 0 : request.getPage();
        int size = request.getSize() <= 0 ? 20 : request.getSize();

        long total = count(where, params);
        List<TrafficEvent> all = findAll(where, params);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, all.size());

        List<TrafficEvent> pageContent =
                fromIndex >= all.size() ? new ArrayList<TrafficEvent>() : all.subList(fromIndex, toIndex);

        SpySearchResult result = new SpySearchResult();
        result.setContent(pageContent);
        result.setTotalElements(total);
        result.setPage(page);
        result.setSize(size);
        result.setHasNext(toIndex < all.size());

        return result;
    }

    private String buildWhere(SpySearchRequest request, List<Object> params) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");

        LocalDate fromDate = parseDate(request.getFromDate());
        LocalDate toDate = parseDate(request.getToDate());

        if (fromDate != null) {
            where.append(" AND event_time >= ? ");
            params.add(Timestamp.valueOf(fromDate.atStartOfDay()));
        }

        if (toDate != null) {
            where.append(" AND event_time < ? ");
            params.add(Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
        }

        if (!isBlank(request.getMethod())) {
            where.append(" AND LOWER(method) = LOWER(?) ");
            params.add(request.getMethod());
        }

        if (request.getStatus() != null) {
            where.append(" AND status = ? ");
            params.add(request.getStatus());
        }

        if (!isBlank(request.getUri())) {
            where.append(" AND LOWER(uri) LIKE LOWER(?) ");
            params.add("%" + request.getUri() + "%");
        }

        if (!isBlank(request.getRequestBody())) {
            where.append(" AND LOWER(request_body) LIKE LOWER(?) ");
            params.add("%" + request.getRequestBody() + "%");
        }

        if (!isBlank(request.getResponseBody())) {
            where.append(" AND LOWER(response_body) LIKE LOWER(?) ");
            params.add("%" + request.getResponseBody() + "%");
        }

        if (!isBlank(request.getText())) {
            where.append(" AND ( ");
            where.append(" LOWER(COALESCE(uri, '')) LIKE LOWER(?) ");
            where.append(" OR LOWER(COALESCE(method, '')) LIKE LOWER(?) ");
            where.append(" OR LOWER(COALESCE(request_body, '')) LIKE LOWER(?) ");
            where.append(" OR LOWER(COALESCE(response_body, '')) LIKE LOWER(?) ");
            where.append(" ) ");

            String text = "%" + request.getText() + "%";
            params.add(text);
            params.add(text);
            params.add(text);
            params.add(text);
        }

        return where.toString();
    }

    private long count(String where, List<Object> params) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + where;

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }

        } catch (Exception e) {
            System.err.println("Traffic Spy JdbcStore count error : " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    private List<TrafficEvent> findAll(String where, List<Object> params) {
        String sql =
                "SELECT event_json FROM " + TABLE_NAME +
                        where +
                        " ORDER BY event_time DESC, id DESC";

        List<TrafficEvent> result = new ArrayList<TrafficEvent>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String json = rs.getString("event_json");
                    result.add(mapper.readValue(json, TrafficEvent.class));
                }
            }

        } catch (Exception e) {
            System.err.println("Traffic Spy JdbcStore search error : " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private int bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        int index = 1;

        for (Object param : params) {
            if (param instanceof Timestamp) {
                ps.setTimestamp(index++, (Timestamp) param);
            } else if (param instanceof Integer) {
                ps.setInt(index++, (Integer) param);
            } else if (param instanceof Long) {
                ps.setLong(index++, (Long) param);
            } else {
                ps.setString(index++, String.valueOf(param));
            }
        }

        return index;
    }

    private void setTimestamp(PreparedStatement ps, int index, LocalDateTime value) throws Exception {
        if (value == null) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private LocalDate parseDate(String value) {
        if (isBlank(value)) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}