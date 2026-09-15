package com.superbuilt.mini.notification;

import com.superbuilt.mini.action.Action;
import com.superbuilt.mini.action.ActionRepository;
import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.issue.IssueRepository;
import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MorningBriefService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM uuuu");

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final ActionRepository actionRepository;
    private final EmailNotificationService emailNotificationService;
    private final String recipient;

    public MorningBriefService(
            ProjectRepository projectRepository,
            IssueRepository issueRepository,
            ActionRepository actionRepository,
            EmailNotificationService emailNotificationService,
            @Value("${app.morning-brief.recipient}") String recipient
    ) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.actionRepository = actionRepository;
        this.emailNotificationService = emailNotificationService;
        this.recipient = recipient;
    }

    @Transactional(readOnly = true)
    public void sendMorningBriefs() {
        projectRepository.findAll()
                .forEach(this::sendMorningBriefForProject);
    }

    private void sendMorningBriefForProject(Project project) {
        List<Issue> issues =
                issueRepository.findByProjectId(project.getId());

        List<Action> actions =
                actionRepository.findByIssueProjectId(project.getId());

        emailNotificationService.sendMorningBrief(
                project.getId(),
                recipient,
                "Morning Brief - " + project.getName(),
                buildEmailBody(project, issues, actions)
        );
    }

    private String buildEmailBody(
            Project project,
            List<Issue> issues,
            List<Action> actions
    ) {
        StringBuilder body = new StringBuilder("""
                SuperBuilt Mini Morning Brief
                ============================

                PROJECT: %s
                TOTAL ISSUES: %d
                TOTAL ACTIONS: %d

                ISSUES
                ------
                """.formatted(
                project.getName(),
                issues.size(),
                actions.size()
        ));

        if (issues.isEmpty()) {
            body.append("No issues recorded.\n");
        } else {
            for (Issue issue : issues) {
                body.append("- [")
                        .append(issue.getSeverity())
                        .append("] ")
                        .append(issue.getTitle())
                        .append(" | Status: ")
                        .append(issue.getStatus())
                        .append(" | Decision required: ")
                        .append(issue.isRequiresDecision() ? "Yes" : "No")
                        .append("\n");
            }
        }

        body.append("\nACTIONS\n-------\n");

        if (actions.isEmpty()) {
            body.append("No actions recorded.\n");
        } else {
            for (Action action : actions) {
                body.append("- [")
                        .append(action.getPriority())
                        .append("] ")
                        .append(action.getTitle())
                        .append(" | Status: ")
                        .append(action.getStatus())
                        .append(" | Issue: ")
                        .append(action.getIssue().getTitle())
                        .append(" | Assignee: ")
                        .append(action.getAssignee() == null ||
                                action.getAssignee().isBlank()
                                ? "Unassigned"
                                : action.getAssignee())
                        .append(" | Due: ")
                        .append(action.getDueDate() == null
                                ? "No due date"
                                : DATE_FORMAT.format(action.getDueDate()))
                        .append("\n");
            }
        }

        body.append("\nGenerated automatically by SuperBuilt Mini.\n");

        return body.toString();
    }
}