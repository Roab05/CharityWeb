package group3.project.charityweb.service;

import group3.project.charityweb.model.Donation;
import group3.project.charityweb.repository.DonationRepository;
import group3.project.charityweb.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final Logger LOG = LoggerFactory.getLogger(DonationService.class);

    public List<Donation> getAll() {
        LOG.info("Getting all projects");
        return donationRepository.findAll();
    }

    public List<Donation> getByDonorId(String donorId) {
        LOG.info("Getting donations by donors' ID");
        return donationRepository.findByDonorId(donorId);
    }

    public List<Donation> getByProjectId(String projectId) {
        LOG.info("Getting donations by projects' ID");
        return donationRepository.findByProjectId(projectId);
    }

    public Donation add(Donation donation)
    {
        LOG.info("Adding donation: {}", donation);
        donation.setDate(LocalDate.now());
        return donationRepository.save(donation);
    }

}
