package com.superbuilt.mini.document.processing;

import com.superbuilt.mini.document.Document;
import com.superbuilt.mini.document.DocumentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentProcessingController {

    private final DocumentRepository documentRepository;
    private final DocumentProcessor documentProcessor;

    public DocumentProcessingController(
            DocumentRepository documentRepository,
            DocumentProcessor documentProcessor
    ) {
        this.documentRepository = documentRepository;
        this.documentProcessor = documentProcessor;
    }

    @PostMapping("/{documentId}/process")
    public ResponseEntity<Map<String, Object>> processDocument(
            @PathVariable Long documentId
    ) throws IOException {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found with id: " + documentId
                        )
                );

        int chunkCount = documentProcessor.processPdf(document);

        return ResponseEntity.ok(
                Map.of(
                        "documentId", documentId,
                        "status", document.getStatus(),
                        "chunkCount", chunkCount
                )
        );
    }
}