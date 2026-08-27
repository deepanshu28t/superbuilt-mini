package com.superbuilt.mini.action;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @PostMapping("/issues/{issueId}/actions")
    @ResponseStatus(HttpStatus.CREATED)
    public Action createAction(
            @PathVariable Long issueId,
            @RequestBody Action action
    ) {
        return actionService.createAction(issueId, action);
    }

    @GetMapping("/issues/{issueId}/actions")
    public List<Action> getActionsByIssue(
            @PathVariable Long issueId
    ) {
        return actionService.getActionsByIssue(issueId);
    }

    @GetMapping("/projects/{projectId}/actions")
    public List<Action> getActionsByProject(
            @PathVariable Long projectId
    ) {
        return actionService.getActionsByProject(projectId);
    }

    @GetMapping("/projects/{projectId}/actions/pending")
    public List<Action> getPendingActions(
            @PathVariable Long projectId
    ) {
        return actionService.getPendingActions(projectId);
    }

    @GetMapping("/actions/{actionId}")
    public Action getAction(
            @PathVariable Long actionId
    ) {
        return actionService.getActionById(actionId);
    }
}