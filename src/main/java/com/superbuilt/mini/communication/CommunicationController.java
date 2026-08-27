package com.superbuilt.mini.communication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/communications")
public class CommunicationController {

    private final CommunicationService communicationService;

    public CommunicationController(
            CommunicationService communicationService
    ) {
        this.communicationService = communicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Communication createCommunication(
            @PathVariable Long projectId,
            @RequestBody Communication communication
    ) {
        return communicationService.createCommunication(
                projectId,
                communication
        );
    }

    @GetMapping
    public List<Communication> getCommunications(
            @PathVariable Long projectId
    ) {
        return communicationService.getCommunicationsByProject(projectId);
    }

    @GetMapping("/unprocessed")
    public List<Communication> getUnprocessedCommunications(
            @PathVariable Long projectId
    ) {
        return communicationService.getUnprocessedCommunications(
                projectId
        );
    }

    @GetMapping("/{communicationId}")
    public Communication getCommunication(
            @PathVariable Long communicationId
    ) {
        return communicationService.getCommunicationById(
                communicationId
        );
    }
}