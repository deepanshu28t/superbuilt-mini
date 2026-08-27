package com.superbuilt.mini.communication;

import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationService {

    private final CommunicationRepository communicationRepository;
    private final ProjectRepository projectRepository;

    public CommunicationService(
            CommunicationRepository communicationRepository,
            ProjectRepository projectRepository
    ) {
        this.communicationRepository = communicationRepository;
        this.projectRepository = projectRepository;
    }

    public Communication createCommunication(
            Long projectId,
            Communication communication
    ) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with id: " + projectId
                        )
                );

        communication.setProject(project);

        return communicationRepository.save(communication);
    }

    public List<Communication> getCommunicationsByProject(
            Long projectId
    ) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return communicationRepository.findByProjectId(projectId);
    }

    public List<Communication> getUnprocessedCommunications(
            Long projectId
    ) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return communicationRepository.findByProjectIdAndStatus(
                projectId,
                CommunicationStatus.UNPROCESSED
        );
    }

    public Communication getCommunicationById(Long id) {

        return communicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Communication not found with id: " + id
                        )
                );
    }
}