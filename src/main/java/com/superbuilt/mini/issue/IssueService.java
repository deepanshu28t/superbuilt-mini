package com.superbuilt.mini.issue;
import com.superbuilt.mini.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.stereotype.Service;
import com.superbuilt.mini.ai.IssueDetectionResult;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final IssueEmbeddingService issueEmbeddingService;

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            IssueEmbeddingService issueEmbeddingService
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.issueEmbeddingService=issueEmbeddingService;
    }

    public Issue createIssue(Long projectId, Issue issue) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found with id: " + projectId
                        )
                );

        issue.setProject(project);

        return issueRepository.save(issue);
    }

    @Transactional
    public Issue updateIssueStatus(
            Long issueId,
            IssueStatus status
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + issueId
                        )
                );

        issue.setStatus(status);

        return issueRepository.save(issue);
    }

    public List<Issue> getIssuesByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }

        return issueRepository.findByProjectId(projectId);
    }

    public List<Issue> getOpenIssues(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }

        return issueRepository.findByProjectIdAndStatus(
                projectId,
                IssueStatus.OPEN
        );
    }

    public List<Issue> getDecisionRequiredIssues(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }

        return issueRepository
                .findByProjectIdAndRequiresDecisionTrue(projectId);
    }

    public Issue getIssueById(Long id) {

        return issueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found with id: " + id
                        )
                );
    }

    @Transactional
    public Issue createIssueFromAiDetection(
            Long projectId,
            IssueDetectionResult result
    ) {

        Issue issue = Issue.builder()
                .title(result.title())
                .description(result.description())
                .type(IssueType.valueOf(result.issueType()))
                .severity(IssueSeverity.valueOf(result.severity()))
                .status(IssueStatus.OPEN)
                .source(IssueSource.AI_DETECTION)
                .requiresDecision(result.requiresDecision())
                .confidenceScore(result.confidenceScore())
                .riskScore(result.riskScore())
                .build();

        Issue savedIssue = createIssue(projectId, issue);

        // Create semantic memory for this issue
        issueEmbeddingService.createEmbedding(savedIssue);

        return savedIssue;
    }
}