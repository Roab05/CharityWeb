package group3.project.charityweb.service;

import group3.project.charityweb.dto.ProjectUpdateDto;
import group3.project.charityweb.exception.DonorNotFoundException;
import group3.project.charityweb.exception.ProjectNotFoundException;
import group3.project.charityweb.model.Project;
import group3.project.charityweb.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Project> getAll() {
        LOG.info("Getting all projects");
        return projectRepository.findAll();
    }

    public Project upload(Project project) {
        LOG.info("Uploading project: {}", project);
        project.setCurrentAmount(0L);
        project.setDonationCount(0L);
        return projectRepository.save(project);
    }

    public Project updateAmount(ProjectUpdateDto projectUpdateDto)
    {
        LOG.info("Updating project amount: {}", projectUpdateDto.getAmount());
        Project project = projectRepository.findById(projectUpdateDto.getId()).orElseThrow(
            () -> new ProjectNotFoundException("Project not found with ID: " + projectUpdateDto.getId()));
        project.setCurrentAmount(project.getCurrentAmount() + projectUpdateDto.getAmount());
        project.setDonationCount(project.getDonationCount() + 1);
        return projectRepository.save(project);
    }

    public Long getCurrentAmount(String id)
    {
        LOG.info("Getting current amount for project: {}", id);
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));
        return project.getCurrentAmount();
    }
}
