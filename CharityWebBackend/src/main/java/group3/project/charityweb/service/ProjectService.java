package group3.project.charityweb.service;

import group3.project.charityweb.exception.DonorNotFoundException;
import group3.project.charityweb.exception.ProjectNotFoundException;
import group3.project.charityweb.model.Project;
import group3.project.charityweb.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final Logger LOG = LoggerFactory.getLogger(ProjectService.class);


    public Project getById(String id) {
        LOG.info("Getting project by ID: {}", id);
        return projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));
    }

    public Project upload(Project project) {
        LOG.info("Uploading project: {}", project);
        project.setFundRaised((long) 0);
        return projectRepository.save(project);
    }
}
