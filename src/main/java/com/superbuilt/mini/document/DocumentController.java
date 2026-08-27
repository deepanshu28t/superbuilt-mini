package com.superbuilt.mini.document;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Document createDocument(
            @PathVariable Long projectId,
            @RequestBody Document document
    ) {
        return documentService.createDocument(projectId, document);
    }

    @GetMapping
    public List<Document> getDocumentsByProject(
            @PathVariable Long projectId
    ) {
        return documentService.getDocumentsByProject(projectId);
    }

    @GetMapping("/{documentId}")
    public Document getDocumentById(
            @PathVariable Long documentId
    ) {
        return documentService.getDocumentById(documentId);
    }
}