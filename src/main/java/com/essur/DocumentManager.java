package com.essur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc.
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
@AllArgsConstructor
public class DocumentManager {
    private final Map<String, Document> documentStorage;

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(String.valueOf(UUID.randomUUID()));
        }
        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return new LinkedList<>(documentStorage.values());
        }
        return documentStorage.values().stream()
                .filter(doc -> validateDocument(doc, request))
                .collect(Collectors.toList());
    }

    private boolean validateDocument(Document doc, SearchRequest request) {
        return (request.getTitlePrefixes() == null ||
                request.getTitlePrefixes().stream().anyMatch(p -> doc.getTitle() != null && doc.getTitle().startsWith(p)))
               && (request.getContainsContents() == null ||
                   request.getContainsContents().stream().anyMatch(c -> doc.getContent() != null && doc.getContent().contains(c)))
               && (request.getAuthorIds() == null ||
                   request.getAuthorIds().contains(doc.getAuthor().getId()))
               && (request.getCreatedFrom() == null ||
                   !doc.getCreated().isBefore(request.getCreatedFrom()))
               && (request.getCreatedTo() == null ||
                   !doc.getCreated().isAfter(request.getCreatedTo()));
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}