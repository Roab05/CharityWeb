package group3.project.charityweb.controller;

import group3.project.charityweb.model.Donation;
import group3.project.charityweb.service.DonationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/donations/")
public class DonationController {

    public final DonationService donationService;

    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        List<Donation> donations = donationService.getAll();
        return ResponseEntity.ok(donations);
    }

    @GetMapping(path = "/donors/{id}")
    public ResponseEntity<List<Donation>> getDonationsByDonorId(@PathVariable(value = "id") String donorId) {
        List<Donation> donations = donationService.getByDonorId(donorId);
        return ResponseEntity.ok(donations);
    }

    @GetMapping(path = "/projects/{id}")
    public ResponseEntity<List<Donation>> getDonationsByProjectId(@PathVariable(value = "id") String projectId) {
        List<Donation> donations = donationService.getByProjectId(projectId);
        return ResponseEntity.ok(donations);
    }

    @PostMapping(path = "add")
    public ResponseEntity<Donation> addDonation(@RequestBody Donation donation) {
        Donation newDonation = donationService.add(donation);
        return new ResponseEntity<>(newDonation, HttpStatus.CREATED);
    }
}
