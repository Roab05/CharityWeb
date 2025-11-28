package group3.project.charityweb.controller;

import group3.project.charityweb.model.Donations;
import group3.project.charityweb.service.DonationsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/donations/")
public class DonationsController {

    public final DonationsService donationsService;

    @GetMapping
    public ResponseEntity<List<Donations>> getAllDonations() {
        List<Donations> donations = donationsService.getAll();
        return ResponseEntity.ok(donations);
    }

    @GetMapping(path = "/users/{id}")
    public ResponseEntity<List<Donations>> getDonationsByUserId(@PathVariable(value = "id") String userId) {
        List<Donations> donations = donationsService.getAllByUserId(userId);
        return ResponseEntity.ok(donations);
    }

    @GetMapping(path = "/projects/{id}")
    public ResponseEntity<List<Donations>> getDonationsByProjectId(@PathVariable(value = "id") String projectId) {
        List<Donations> donations = donationsService.getAllByProjectId(projectId);
        return ResponseEntity.ok(donations);
    }

    @PostMapping(path = "add")
    public ResponseEntity<Donations> addDonation(@RequestBody Donations donation) {
        Donations newDonation = donationsService.add(donation);
        return new ResponseEntity<>(newDonation, HttpStatus.CREATED);
    }
}
