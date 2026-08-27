package com.superbuilt.mini.action;

import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.issue.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionService {

    private final ActionRepository actionRepository;
    private final IssueRepository issueRepository;

    public ActionService(
            ActionRepository actionRepository,
            IssueRepository issueRepository
    ) {
        this.actionRepository = actionRepository;
        this.issueRepository = issueRepository;
    }

    public Action createAction(Long issueId, Action action) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id: " + issueId
                        )
                );

        action.setIssue(issue);

        return actionRepository.save(action);
    }

    public List<Action> getActionsByIssue(Long issueId) {

        if (!issueRepository.existsById(issueId)) {
            throw new RuntimeException(
                    "Issue not found with id: " + issueId
            );
        }

        return actionRepository.findByIssueId(issueId);
    }

    public List<Action> getActionsByProject(Long projectId) {

        return actionRepository.findByIssueProjectId(projectId);
    }

    public List<Action> getPendingActions(Long projectId) {

        return actionRepository.findByIssueProjectIdAndStatusNot(
                projectId,
                ActionStatus.COMPLETED
        );
    }

    public Action getActionById(Long id) {

        return actionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Action not found with id: " + id
                        )
                );
    }
}