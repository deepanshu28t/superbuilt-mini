package com.superbuilt.mini.dashboard;

import com.superbuilt.mini.action.ActionRepository;
import com.superbuilt.mini.action.ActionStatus;
import com.superbuilt.mini.document.DocumentRepository;
import com.superbuilt.mini.document.DocumentStatus;
import com.superbuilt.mini.exception.ResourceNotFoundException;
import com.superbuilt.mini.issue.IssueRepository;
import com.superbuilt.mini.issue.IssueSeverity;
import com.superbuilt.mini.issue.IssueStatus;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final IssueRepository issueRepository;
    private final ActionRepository actionRepository;

    public DashboardService(
            ProjectRepository projectRepository,
            DocumentRepository documentRepository,
            IssueRepository issueRepository,
            ActionRepository actionRepository
    ) {
        this.projectRepository = projectRepository;
        this.documentRepository = documentRepository;
        this.issueRepository = issueRepository;
        this.actionRepository = actionRepository;
    }

    public DashboardResponse getDashboard(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }

        long totalDocuments =
                documentRepository.countByProjectId(projectId);

        long processedDocuments =
                documentRepository.countByProjectIdAndStatus(
                        projectId,
                        DocumentStatus.PROCESSED
                );

        long totalIssues =
                issueRepository.countByProjectId(projectId);

        long openIssues =
                issueRepository.countByProjectIdAndStatus(
                        projectId,
                        IssueStatus.OPEN
                );

        long highSeverityIssues =
                issueRepository.countByProjectIdAndSeverity(
                        projectId,
                        IssueSeverity.HIGH
                );

        long decisionRequiredIssues =
                issueRepository
                        .countByProjectIdAndRequiresDecisionTrue(
                                projectId
                        );

        long pendingActions =
                actionRepository
                        .countByIssueProjectIdAndStatusNot(
                                projectId,
                                ActionStatus.COMPLETED
                        );

        return new DashboardResponse(
                totalDocuments,
                processedDocuments,
                totalIssues,
                openIssues,
                highSeverityIssues,
                decisionRequiredIssues,
                pendingActions
        );
    }
}