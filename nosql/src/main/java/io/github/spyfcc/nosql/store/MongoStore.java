package io.github.spyfcc.nosql.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.github.spyfcc.core.dto.SpySearchRequest;
import io.github.spyfcc.core.dto.SpySearchResult;
import io.github.spyfcc.core.event.TrafficEvent;
import io.github.spyfcc.core.store.SpyStore;

public class MongoStore implements SpyStore {

    private static final String COLLECTION_NAME = "spy_traffic_log";

    private final MongoTemplate mongoTemplate;

    public MongoStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;

        if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
            mongoTemplate.createCollection(COLLECTION_NAME);
        }
    }

    @Override
    public void save(TrafficEvent event) {
        try {
            if (event.getId() == null || event.getId().trim().isEmpty()) {
                event.setId(UUID.randomUUID().toString());
            }

            if (event.getTimestamp() == null) {
                event.setTimestamp(LocalDateTime.now());
            }

            mongoTemplate.save(event, COLLECTION_NAME);

        } catch (Exception e) {
            System.err.println("Traffic Spy MongoStore save error : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public SpySearchResult search(SpySearchRequest request) {
        Query query = buildQuery(request);

        int page = request.getPage() == null || request.getPage() < 0 ? 0 : request.getPage();
        int size = request.getSize() <= 0 ? 20 : request.getSize();

        long total = mongoTemplate.count(query, TrafficEvent.class, COLLECTION_NAME);

        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.skip((long) page * size);
        query.limit(size);

        List<TrafficEvent> content =
                mongoTemplate.find(query, TrafficEvent.class, COLLECTION_NAME);

        SpySearchResult result = new SpySearchResult();
        result.setContent(content);
        result.setTotalElements(total);
        result.setPage(page);
        result.setSize(size);
        result.setHasNext(((long) page * size) + content.size() < total);

        return result;
    }

    private Query buildQuery(SpySearchRequest request) {
        List<Criteria> criteriaList = new ArrayList<Criteria>();

        LocalDate fromDate = parseDate(request.getFromDate());
        LocalDate toDate = parseDate(request.getToDate());

        if (fromDate != null && toDate != null) {
            criteriaList.add(Criteria.where("timestamp")
                    .gte(fromDate.atStartOfDay())
                    .lt(toDate.plusDays(1).atStartOfDay()));
        } else if (fromDate != null) {
            criteriaList.add(Criteria.where("timestamp")
                    .gte(fromDate.atStartOfDay()));
        } else if (toDate != null) {
            criteriaList.add(Criteria.where("timestamp")
                    .lt(toDate.plusDays(1).atStartOfDay()));
        }

        if (!isBlank(request.getMethod())) {
            criteriaList.add(Criteria.where("method")
                    .regex("^" + escapeRegex(request.getMethod()) + "$", "i"));
        }

        if (request.getStatus() != null) {
            criteriaList.add(Criteria.where("status").is(request.getStatus()));
        }

        if (!isBlank(request.getUri())) {
            criteriaList.add(Criteria.where("uri")
                    .regex(escapeRegex(request.getUri()), "i"));
        }

        if (!isBlank(request.getRequestBody())) {
            criteriaList.add(Criteria.where("requestBody")
                    .regex(escapeRegex(request.getRequestBody()), "i"));
        }

        if (!isBlank(request.getResponseBody())) {
            criteriaList.add(Criteria.where("responseBody")
                    .regex(escapeRegex(request.getResponseBody()), "i"));
        }

        if (!isBlank(request.getText())) {
            String text = escapeRegex(request.getText());

            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("uri").regex(text, "i"),
                    Criteria.where("method").regex(text, "i"),
                    Criteria.where("requestBody").regex(text, "i"),
                    Criteria.where("responseBody").regex(text, "i")
            ));
        }

        Query query = new Query();

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(
                    criteriaList.toArray(new Criteria[criteriaList.size()])
            ));
        }

        return query;
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

    private String escapeRegex(String value) {
        return value.replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("?", "\\?")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("|", "\\|")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }
}