package com.superbuilt.mini.document.processing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentProcessingController {

    private final DocumentProcessor documentProcessor;

    public DocumentProcessingController(
            DocumentProcessor documentProcessor
    ) {
        this.documentProcessor = documentProcessor;
    }

    @PostMapping("/{documentId}/process")
    public ResponseEntity<Map<String, Object>> processDocument(
            @PathVariable Long documentId
    ) throws IOException {

        int chunkCount = documentProcessor.processPdf(documentId);

        return ResponseEntity.ok(
                Map.of(
                        "documentId", documentId,
                        "status", "PROCESSED",
                        "chunkCount", chunkCount
                )
        );
    }
}
