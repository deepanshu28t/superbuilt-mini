package com.superbuilt.mini.issue;

import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;

    public IssueService(
            IssueRepository issueRepository,
            ProjectRepository projectRepository
    ) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
    }

    public Issue createIssue(Long projectId, Issue issue) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with id: " + projectId
                        )
                );

        issue.setProject(project);

        return issueRepository.save(issue);
    }

    public List<Issue> getIssuesByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return issueRepository.findByProjectId(projectId);
    }

    public List<Issue> getOpenIssues(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
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
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return issueRepository
                .findByProjectIdAndRequiresDecisionTrue(projectId);
    }

    public Issue getIssueById(Long id) {

        return issueRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id: " + id
                        )
                );
    }
}