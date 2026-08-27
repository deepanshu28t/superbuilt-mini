package com.superbuilt.mini.rfi;

import com.superbuilt.mini.project.Project;
import com.superbuilt.mini.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RFIService {

    private final RFIRepository rfiRepository;
    private final ProjectRepository projectRepository;

    public RFIService(
            RFIRepository rfiRepository,
            ProjectRepository projectRepository
    ) {
        this.rfiRepository = rfiRepository;
        this.projectRepository = projectRepository;
    }

    public RFI createRFI(Long projectId, RFI rfi) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with id: " + projectId
                        )
                );

        rfi.setProject(project);

        return rfiRepository.save(rfi);
    }

    public List<RFI> getRFIsByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return rfiRepository.findByProjectId(projectId);
    }

    public List<RFI> getOpenRFIs(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException(
                    "Project not found with id: " + projectId
            );
        }

        return rfiRepository.findByProjectIdAndStatus(
                projectId,
                RFIStatus.OPEN
        );
    }

    public RFI getRFIById(Long id) {

        return rfiRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "RFI not found with id: " + id
                        )
                );
    }
}