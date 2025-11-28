package group3.project.charityweb.service;

import group3.project.charityweb.dto.ProjectUpdateDto;
import group3.project.charityweb.exception.ProjectNotFoundException;
import group3.project.charityweb.model.Donations;
import group3.project.charityweb.model.Projects;
import group3.project.charityweb.repository.DonationsRepository;
import group3.project.charityweb.repository.ProjectsRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectsService {

    private final ProjectsRepository projectsRepository;
    private final DonationsRepository donationsRepository;
    private final Logger LOG = LoggerFactory.getLogger(ProjectsService.class);


    public Projects getById(String id) {
        LOG.info("Getting project by ID: {}", id);
        return projectsRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));
    }

    public List<Projects> getAll() {
        LOG.info("Getting all projects");
        return projectsRepository.findAllByOrderByStateAscCreatedAtDesc();
    }

    public Projects upload(Projects project) {
        LOG.info("Uploading project: {}", project);
        project.setCreatedAt(LocalDateTime.now());
        return projectsRepository.save(project);
    }

    public Projects updateAmount(ProjectUpdateDto projectUpdateDto)
    {
        LOG.info("Updating project amount: {}", projectUpdateDto.getAmount());
        Projects project = projectsRepository.findById(projectUpdateDto.getId()).orElseThrow(
            () -> new ProjectNotFoundException("Project not found with ID: " + projectUpdateDto.getId()));
        return projectsRepository.save(project);
    }

    public Long getCurrentAmount(String id)
    {
        LOG.info("Getting current amount for project: {}", id);
        projectsRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));

        List<Donations> donations = donationsRepository.findAllByProjectIdOrderByDateTimeDesc(id);
        Long currentAmount = 0L;
        for (Donations donation : donations) {
            currentAmount += donation.getAmount();
        }
        return currentAmount;
    }

    public Long getDonationCount(String id)
    {
        LOG.info("Getting donation count for project: {}", id);
        projectsRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));
        List<Donations> donations = donationsRepository.findAllByProjectIdOrderByDateTimeDesc(id);

        return (long)donations.size();
    }

    public Long getDaysLeft(String id)
    {
        LOG.info("Getting days left for project: {}", id);
        Projects project = projectsRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));
        return ChronoUnit.DAYS.between(LocalDate.now(), project.getEndDate()) + 1L;
    }

    public Integer setAndGetState(String id) {

        LOG.info("Getting state for project: {}", id);
        Projects project = projectsRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));

        LocalDate today = LocalDate.now();
        LocalDate start = project.getStartDate();
        LocalDate end = project.getEndDate();

        if (start == null || end == null) {
            return 2; // da ket thuc
        }

        if (today.isBefore(start)) {
            project.setState(0); // chuan bi
        } else if (!today.isAfter(end)) {
            project.setState(1); // dang hoat dong
        } else {
            project.setState(2); // da ket thuc
        }

        projectsRepository.save(project);
        return project.getState();
    }

    public Projects updateInfo(ProjectUpdateDto projectUpdateDto)
    {
        LOG.info("Updating project by ID: {}", projectUpdateDto.getId());
        Projects project = projectsRepository.findById(projectUpdateDto.getId()).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + projectUpdateDto.getId()));

        project.setName(projectUpdateDto.getName());
        project.setCategory(projectUpdateDto.getCategory());
        project.setDescription(projectUpdateDto.getDescription());
        project.setTargetAmount(projectUpdateDto.getTargetAmount());
        project.setImageUrl(projectUpdateDto.getImageUrl());
        project.setEndDate(projectUpdateDto.getEndDate());

        return projectsRepository.save(project);
    }

    public void deleteById(String id) {
        LOG.info("Deleting project by ID: {}", id);
        Projects project = projectsRepository.findById(id).orElseThrow(
                () -> new ProjectNotFoundException("Project not found with ID: " + id));
        projectsRepository.delete(project);
    }
}
