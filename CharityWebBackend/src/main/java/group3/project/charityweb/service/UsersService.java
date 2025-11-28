package group3.project.charityweb.service;

import group3.project.charityweb.dto.UserUpdateDto;
import group3.project.charityweb.dto.LoginDto;
import group3.project.charityweb.exception.UserNotFoundException;
import group3.project.charityweb.model.Donations;
import group3.project.charityweb.model.Users;
import group3.project.charityweb.repository.DonationsRepository;
import group3.project.charityweb.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsersService {

    private final Logger LOG = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository usersRepository;
    private final DonationsRepository donationsRepository;
    private final PasswordEncoder passwordEncoder;

    public Users getById(String id) {
        LOG.info("Getting user by ID: {}", id);
        return usersRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id));
    }

    public Users registerAccount(Users newUser) {
        LOG.info("Registering new user account with email: {}", newUser.getEmail());

        // check if email is already used
        usersRepository.findByEmail(newUser.getEmail()).ifPresent(d -> {
            LOG.warn("Registration failed: Email {} is already in use", newUser.getEmail());
            throw new IllegalStateException("Email is already in use");
        });

        // check if phone number is already used
        usersRepository.findByPhoneNumber(newUser.getPhoneNumber()).ifPresent(d -> {
            LOG.warn("Registration failed: Phone number {} is already in use", newUser.getPhoneNumber());
            throw new IllegalStateException("Phone number is already in use");
        });

        // check if display name is already used
        usersRepository.findByDisplayName(newUser.getDisplayName()).ifPresent(d -> {
            LOG.warn("Registration failed: Display name {} is already in use", newUser.getDisplayName());
            throw new IllegalStateException("Display name is already in use");
        });

        // password hashing and saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setAdmin(false);

        Users savedUser = usersRepository.save(newUser);
        LOG.info("Successfully registered user account with ID: {}", savedUser.getId());
        return savedUser;
    }

    public Users loginByEmail(LoginDto loginDto) throws FailedLoginException {
        LOG.info("Attempting login for email: {}", loginDto.getEmail());

        Users user = usersRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new FailedLoginException("Invalid email or password"));

        // passwords match
        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            LOG.info("Login successful for user ID: {}", user.getId());
            return user;
        } else {
            LOG.warn("Login failed for email: {}, incorrect password", loginDto.getEmail());
            throw new FailedLoginException("Invalid email or password");
        }
    }


    public Users updateInfoById(UserUpdateDto userUpdateDto) {
        LOG.info("Updating info for user by ID: {}", userUpdateDto.getId());
        Users userToUpdate = getById(userUpdateDto.getId());

        // check if the new phone number is taken by another user
        Optional<Users> existingByPhone = usersRepository.findByPhoneNumber(userUpdateDto.getPhoneNumber());
        if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(userToUpdate.getId())) {
            LOG.warn("Info update failed: Phone number {} is already used", userUpdateDto.getPhoneNumber());
            throw new IllegalStateException("Phone number is already in use by another account");
        }

        // check if the new display name is taken by another user
        Optional<Users> existingByDisplayName = usersRepository.findByDisplayName(userUpdateDto.getDisplayName());
        if (existingByDisplayName.isPresent() && !existingByDisplayName.get().getId().equals(userToUpdate.getId())) {
            LOG.warn("Info update failed: Display name {} is already used", userUpdateDto.getDisplayName());
            throw new IllegalStateException("Display name is already in use by another account");
        }

        userToUpdate.setPhoneNumber(userUpdateDto.getPhoneNumber());
        userToUpdate.setDisplayName(userUpdateDto.getDisplayName());

        return usersRepository.save(userToUpdate);
    }

    public Long getTotalDonation(String id)
    {
        LOG.info("Getting total donations for user by ID: {}", id);
        List<Donations> donations = donationsRepository.findAllByUserIdOrderByDateTimeDesc(id);
        Long totalDonations = 0L;
        for (Donations donation : donations) {
            totalDonations += donation.getAmount();
        }
        return totalDonations;
    }

    public boolean updatePasswordById(UserUpdateDto userUpdateDto) {
        LOG.info("Attempting to update password for user ID: {}", userUpdateDto.getId());
        Users userToUpdate = getById(userUpdateDto.getId());

        // verify current password
        if (!passwordEncoder.matches(userUpdateDto.getCurrentPassword(), userToUpdate.getPassword())) {
            LOG.warn("Password update failed for user ID: {}. Incorrect current password", userUpdateDto.getId());
            // Throw an exception instead of returning false for better error handling on the client-side
            throw new SecurityException("Incorrect current password");
        }

        // new password hashing and saving
        userToUpdate.setPassword(passwordEncoder.encode(userUpdateDto.getNewPassword()));
        usersRepository.save(userToUpdate);

        LOG.info("Password successfully updated for user ID: {}", userUpdateDto.getId());
        return true;
    }
}