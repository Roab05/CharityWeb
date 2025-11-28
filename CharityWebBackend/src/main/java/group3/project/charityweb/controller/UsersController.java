package group3.project.charityweb.controller;

import group3.project.charityweb.dto.UserUpdateDto;
import group3.project.charityweb.dto.LoginDto;
import group3.project.charityweb.model.Users;
import group3.project.charityweb.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.FailedLoginException;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/users/")
public class UsersController {

    private final UsersService usersService;

    @GetMapping(path = "{id}")
    public ResponseEntity<Users> getUserById(@PathVariable("id") String id) {
        Users user = usersService.getById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Users> registerAccount(@RequestBody Users newUser) {
        Users registeredUser = usersService.registerAccount(newUser);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<Users> login(@RequestBody LoginDto loginDto) throws FailedLoginException {
        Users user = usersService.loginByEmail(loginDto);
        if (user != null)
            return ResponseEntity.ok(user);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PutMapping(path = "/info_update")
    public ResponseEntity<Users> updateUserInfo(@RequestBody UserUpdateDto userUpdateDto) {
        Users updatedUser = usersService.updateInfoById(userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping(path = "/{id}/total_donation")
    public ResponseEntity<Long> getUserTotalDonation(@PathVariable("id") String id) {
        Long totalDonation = usersService.getTotalDonation(id);
        return ResponseEntity.ok(totalDonation);
    }

    @PutMapping(path = "/password_update")
    public ResponseEntity<Users> updatePassword(@RequestBody UserUpdateDto userUpdateDto) {
        boolean updateSuccess = usersService.updatePasswordById(userUpdateDto);
        if (!updateSuccess)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
