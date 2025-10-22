package group3.project.charityweb.controller;

import group3.project.charityweb.dto.LoginDto;
import group3.project.charityweb.dto.PasswordUpdateDto;
import group3.project.charityweb.model.Donor;
import group3.project.charityweb.service.DonorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.FailedLoginException;

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
        boolean loginSuccessful = donorService.loginByEmail(loginDto);
        if (loginSuccessful)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }



    @PutMapping(path = "/{id}/info_update")
    public ResponseEntity<Donor> updateDonorInfo(@PathVariable("id") String id,
                                                 @RequestBody Donor donor) {
        Donor updatedDonor = donorService.updateInfoById(id, donor);
        return ResponseEntity.ok(updatedDonor);
    }



    @PutMapping(path = "/{id}/password_update")
    public ResponseEntity<Donor> updatePassword(@PathVariable("id") String id, @RequestBody PasswordUpdateDto passwordUpdateDto) {
        boolean updatedSuccessful = donorService.updatePasswordById(id, passwordUpdateDto);
        if (!updatedSuccessful)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
