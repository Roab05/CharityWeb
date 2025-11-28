package group3.project.charityweb.service;

import group3.project.charityweb.exception.ProjectNotFoundException;
import group3.project.charityweb.model.Donations;
import group3.project.charityweb.model.Projects;
import group3.project.charityweb.repository.DonationsRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class DonationsService {

    private final DonationsRepository donationsRepository;
    private final Logger LOG = LoggerFactory.getLogger(DonationsService.class);

    public List<Donations> getAll() {
        LOG.info("Getting all projects");
        return donationsRepository.findAll();
    }

    public List<Donations> getAllByUserId(String userId) {
        LOG.info("Getting donations by donors' ID");
        return donationsRepository.findAllByUserIdOrderByDateTimeDesc(userId);
    }

    public List<Donations> getAllByProjectId(String projectId) {
        LOG.info("Getting donations by projects' ID");
        return donationsRepository.findAllByProjectIdOrderByDateTimeDesc(projectId);
    }

    public Donations add(Donations donation)
    {
        LOG.info("Adding donation: {}", donation);

        donation.setDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return donationsRepository.save(donation);
    }
}
