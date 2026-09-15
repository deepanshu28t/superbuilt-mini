package com.superbuilt.mini.document;

import com.superbuilt.mini.exception.ResourceNotFoundException;
import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import com.superbuilt.mini.document.processing.DocumentProcessor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.superbuilt.mini.ai.CoordinationAgentService;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final DocumentProcessor documentProcessor;
    private final DocumentChunkRepository documentChunkRepository;
    private final CoordinationAgentService coordinationAgentService;
    public DocumentService(
            DocumentRepository documentRepository,
            ProjectRepository projectRepository,
            DocumentProcessor documentProcessor,
            DocumentChunkRepository documentChunkRepository,
            CoordinationAgentService coordinationAgentService
    ) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.documentProcessor = documentProcessor;
        this.documentChunkRepository = documentChunkRepository;
        this.coordinationAgentService=coordinationAgentService;
    }

    public Document createDocument(
            Long projectId,
            Document document
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        )
                );

        document.setProject(project);

        Document savedDocument =
                documentRepository.save(document);

        // Step 1: Process PDF and store vector chunks.
        try {
            documentProcessor.processPdf(savedDocument.getId());
        } catch (IOException | RuntimeException e) {
            savedDocument.setStatus(DocumentStatus.FAILED);
            documentRepository.save(savedDocument);

            throw new IllegalStateException(
                    "Document was created but PDF processing failed: "
                            + savedDocument.getId(),
                    e
            );
        }

        // Step 2: Analyze newly processed document against project knowledge.
        coordinationAgentService.detectAndCreate(
                projectId,
                buildAutomaticAnalysisQuestion(savedDocument)
        );

        return getDocumentById(savedDocument.getId());
    }
    private String buildAutomaticAnalysisQuestion(
            Document document
    ) {
        return """
            Analyze the newly uploaded project document below together with
            the most relevant existing project documents.

            Newly uploaded document:
            Name: %s
            Discipline: %s
            Type: %s
            Description: %s

            Detect a real coordination clash, compliance concern,
            restriction, delay, RFI, or other actionable issue only when
            supported by the project document evidence.

            If an issue is found, create the issue and its recommended action.
            If no issue is supported by evidence, do not create anything.
            """.formatted(
                document.getName(),
                document.getDiscipline(),
                document.getType(),
                document.getDescription() == null
                        ? "No description provided"
                        : document.getDescription()
        );
    }
    public List<Document> getDocumentsByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }

        return documentRepository.findByProjectId(projectId);
    }

    public Document getDocumentById(Long id) {

        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found with id: " + id
                        )                );
    }

    @Transactional
    public void deleteDocument(
            Long projectId,
            Long documentId
    ) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found with id: " + documentId
                        )
                );

        if (!document.getProject().getId().equals(projectId)) {
            throw new ResourceNotFoundException(
                    "Document does not belong to project: " + projectId
            );
        }

        // Delete indexed text and pgvector embeddings first.
        documentChunkRepository.deleteByDocumentId(documentId);

        // Delete the document record.
        documentRepository.delete(document);
    }
}
