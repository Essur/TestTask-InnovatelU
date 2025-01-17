package com.essur;

import com.essur.DocumentManager.Author;
import com.essur.DocumentManager.Document;
import com.essur.DocumentManager.SearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    private DocumentManager documentManager;
    private Author author1;
    private Author author2;

    @BeforeEach
    void setUp() {
        Map<String, Document> initDocs = new HashMap<>();

        author1 = Author.builder()
                .id("1")
                .name("John Doe")
                .build();

        author2 = Author.builder()
                .id("2")
                .name("Jane Smith")
                .build();

        Document doc1 = Document.builder()
                .id("1")
                .title("Test Document")
                .content("This is a test document.")
                .author(author1)
                .created(Instant.parse("2025-01-01T10:00:00Z"))
                .build();

        Document doc2 = Document.builder()
                .id("2")
                .title("Another Test")
                .content("Some other content.")
                .author(author1)
                .created(Instant.parse("2025-01-02T10:00:00Z"))
                .build();

        Document doc3 = Document.builder()
                .id("3")
                .title("Sample Document")
                .content("This document contains sample content.")
                .author(author2)
                .created(Instant.parse("2025-01-03T10:00:00Z"))
                .build();

        initDocs.put(doc1.getId(), doc1);
        initDocs.put(doc2.getId(), doc2);
        initDocs.put(doc3.getId(), doc3);
        documentManager = new DocumentManager(initDocs);
    }

    @Test
    void save_Works_Properly() {
        Document document = Document.builder()
                .title("New Document")
                .content("New document content.")
                .author(author1)
                .created(Instant.now())
                .build();

        Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertNotNull(savedDocument.getCreated());
        assertEquals("New Document", savedDocument.getTitle());
    }

    @Test
    void update_Works_Properly() {
        List<Document> documents = documentManager.search(
                SearchRequest.builder().build());

        Document documentToUpdate = documents.get(0);
        Instant createdTime = documentToUpdate.getCreated();

        documentToUpdate.setContent("Updated content.");
        Document updatedDocument = documentManager.save(documentToUpdate);

        assertEquals(documentToUpdate.getId(), updatedDocument.getId());
        assertEquals(createdTime, updatedDocument.getCreated());
        assertEquals("Updated content.", updatedDocument.getContent());
    }

    @Test
    void search_ByTitlePrefix_returnsMatchDocuments() {
        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(List.of("Test"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Test Document", results.get(0).getTitle());
    }

    @Test
    void search_ByContent_returnsMatchDocuments() {
        SearchRequest request = SearchRequest.builder()
                .containsContents(List.of("sample"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("This document contains sample content.", results.get(0).getContent());
    }

    @Test
    void search_ByDateRange_returnsMatchDocuments() {
        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .createdFrom(Instant.parse("2025-01-01T00:00:00Z"))
                .createdTo(Instant.parse("2025-01-02T23:59:59Z"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getTitle().equals("Test Document")));
        assertTrue(results.stream().anyMatch(doc -> doc.getTitle().equals("Another Test")));
    }

    @Test
    void find_ById_returnsMatchDocuments() {
        List<Document> documents = documentManager.search(
                SearchRequest.builder().build());

        String id = documents.get(0).getId();
        Optional<Document> foundDocument = documentManager.findById(id);

        assertTrue(foundDocument.isPresent());
        assertEquals(documents.get(0).getTitle(), foundDocument.get().getTitle());
    }

    @Test
    void find_ByIdNotFound_returnsEmpty() {
        Optional<Document> foundDocument = documentManager.findById("nonexistent-id");
        assertFalse(foundDocument.isPresent());
    }
}
