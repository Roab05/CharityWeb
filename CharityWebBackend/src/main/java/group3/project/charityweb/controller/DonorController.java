package group3.project.charityweb.controller;

import group3.project.charityweb.dto.DonorUpdateDto;
import group3.project.charityweb.dto.LoginDto;
import group3.project.charityweb.dto.PasswordUpdateDto;
import group3.project.charityweb.model.Donor;
import group3.project.charityweb.service.DonorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.FailedLoginException;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/donors/")
public class DonorController {

    private final DonorService donorService;



    @GetMapping(path = "{id}")
    public ResponseEntity<Donor> getDonor(@PathVariable("id") String id) {
        Donor donor = donorService.getById(id);
        return ResponseEntity.ok(donor);
    }



    @PostMapping(path = "/register")
    public ResponseEntity<Donor> registerAccount(@RequestBody Donor newDonor) {
        Donor registeredDonor = donorService.registerAccount(newDonor);
        return new ResponseEntity<>(registeredDonor, HttpStatus.CREATED);
    }



    @PostMapping(path = "/login")
    public ResponseEntity<Donor> login(@RequestBody LoginDto loginDto) throws FailedLoginException {
        Donor donor = donorService.loginByEmail(loginDto);
        if (donor != null)
            return ResponseEntity.ok(donor);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



    @PutMapping(path = "/info_update")
    public ResponseEntity<Donor> updateDonorInfo(@RequestBody DonorUpdateDto donorUpdateDto) {
        Donor updatedDonor = donorService.updateInfoById(donorUpdateDto);
        return ResponseEntity.ok(updatedDonor);
    }

    @PutMapping(path = "/donation_update")
    public ResponseEntity<Donor> updateDonorTotalDonation(@RequestBody DonorUpdateDto donorUpdateDto) {
        Donor updatedDonor = donorService.updateTotalDonation(donorUpdateDto);
        return ResponseEntity.ok(updatedDonor);
    }

    @PutMapping(path = "/password_update")
    public ResponseEntity<Donor> updatePassword(@RequestBody DonorUpdateDto donorUpdateDto) {
        boolean updatedSuccessful = donorService.updatePasswordById(donorUpdateDto);
        if (!updatedSuccessful)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
