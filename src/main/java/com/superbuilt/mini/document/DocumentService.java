package com.superbuilt.mini.document;

import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            ProjectRepository projectRepository
    ) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
    }

    public Document createDocument(Long projectId, Document document) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found with id: " + projectId)
                );

        document.setProject(project);

        return documentRepository.save(document);
    }

    public List<Document> getDocumentsByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return documentRepository.findByProjectId(projectId);
    }

    public Document getDocumentById(Long id) {

        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Document not found with id: " + id)
                );
    }
}