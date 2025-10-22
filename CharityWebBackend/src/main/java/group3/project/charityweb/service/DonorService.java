package group3.project.charityweb.service;

import group3.project.charityweb.dto.LoginDto;
import group3.project.charityweb.dto.PasswordUpdateDto;
import group3.project.charityweb.exception.DonorNotFoundException;
import group3.project.charityweb.model.Donor;
import group3.project.charityweb.repository.DonorRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DonorService {

    private final Logger LOG = LoggerFactory.getLogger(DonorService.class);
    private final DonorRepository donorRepository;
    private final PasswordEncoder passwordEncoder;


    public Donor getById(String id) {
        LOG.info("Getting donor by ID: {}", id);
        return donorRepository.findById(id).orElseThrow(
                () -> new DonorNotFoundException("Donor not found with ID: " + id));
    }



    public Donor registerAccount(Donor newDonor) {
        LOG.info("Registering new donor account with email: {}", newDonor.getEmail());

        // check if email is already used
        donorRepository.findByEmail(newDonor.getEmail()).ifPresent(d -> {
            LOG.warn("Registration failed: Email {} is already in use", newDonor.getEmail());
            throw new IllegalStateException("Email is already in use");
        });

        // check if phone number is already used
        donorRepository.findByPhoneNumber(newDonor.getPhoneNumber()).ifPresent(d -> {
            LOG.warn("Registration failed: Phone number {} is already in use", newDonor.getPhoneNumber());
            throw new IllegalStateException("Phone number is already in use");
        });

        // check if display name is already used
        donorRepository.findByDisplayName(newDonor.getDisplayName()).ifPresent(d -> {
            LOG.warn("Registration failed: Display name {} is already in use", newDonor.getDisplayName());
            throw new IllegalStateException("Display name is already in use");
        });

        // password hashing and saving
        newDonor.setPassword(passwordEncoder.encode(newDonor.getPassword()));
        newDonor.setTotalDonation(0L);

        Donor savedDonor = donorRepository.save(newDonor);
        LOG.info("Successfully registered donor account with ID: {}", savedDonor.getId());
        return savedDonor;
    }

    public boolean loginByEmail(LoginDto loginDto) throws FailedLoginException {
        LOG.info("Attempting login for email: {}", loginDto.getEmail());

        Donor donor = donorRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new FailedLoginException("Invalid email or password"));

        // passwords match
        if (passwordEncoder.matches(loginDto.getPassword(), donor.getPassword())) {
            LOG.info("Login successful for donor ID: {}", donor.getId());
            return true;
        } else {
            LOG.warn("Login failed for email: {}, incorrect password", loginDto.getEmail());
            throw new FailedLoginException("Invalid email or password");
        }
    }


    public Donor updateInfoById(String id, Donor updatedDonorData) {
        LOG.info("Updating info for donor by ID: {}", id);
        Donor donorToUpdate = getById(id);

        // check if the new phone number is taken by another user
        Optional<Donor> existingByPhone = donorRepository.findByPhoneNumber(updatedDonorData.getPhoneNumber());
        if (existingByPhone.isPresent() && !existingByPhone.get().getId().equals(donorToUpdate.getId())) {
            LOG.warn("Info update failed: Phone number {} is already used", updatedDonorData.getPhoneNumber());
            throw new IllegalStateException("Phone number is already in use by another account");
        }

        // check if the new display name is taken by another user
        Optional<Donor> existingByDisplayName = donorRepository.findByDisplayName(updatedDonorData.getDisplayName());
        if (existingByDisplayName.isPresent() && !existingByDisplayName.get().getId().equals(donorToUpdate.getId())) {
            LOG.warn("Info update failed: Display name {} is already used", updatedDonorData.getDisplayName());
            throw new IllegalStateException("Display name is already in use by another account");
        }

        donorToUpdate.setPhoneNumber(updatedDonorData.getPhoneNumber());
        donorToUpdate.setDisplayName(updatedDonorData.getDisplayName());

        return donorRepository.save(donorToUpdate);
    }



    public boolean updatePasswordById(String id, PasswordUpdateDto passwordUpdateDto) {
        LOG.info("Attempting to update password for donor ID: {}", id);
        Donor donorToUpdate = getById(id);

        // verify current password
        if (!passwordEncoder.matches(passwordUpdateDto.getCurrentPassword(), donorToUpdate.getPassword())) {
            LOG.warn("Password update failed for donor ID: {}. Incorrect current password", id);
            // Throw an exception instead of returning false for better error handling on the client-side
            throw new SecurityException("Incorrect current password");
        }

        // new password hashing and saving
        donorToUpdate.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        donorRepository.save(donorToUpdate);

        LOG.info("Password successfully updated for donor ID: {}", id);
        return true;
    }
}