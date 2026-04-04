package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.DuplicateResourceException;
import group3.project.charityweb.model.dto.request.LoginRequest;
import group3.project.charityweb.model.dto.request.RegisterIndivRequest;
import group3.project.charityweb.model.dto.request.RegisterOrgRequest;
import group3.project.charityweb.model.entity.Individual;
import group3.project.charityweb.model.entity.Organization;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.IndividualRepository;
import group3.project.charityweb.repository.OrganizationRepository;
import group3.project.charityweb.repository.UserRepository;
import group3.project.charityweb.security.JwtTokenProvider;
import group3.project.charityweb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final IndividualRepository individualRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // Logic Đăng ký Cá nhân
    public String registerIndividual(RegisterIndivRequest request) {
        // 1. Kiểm tra tồn tại
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng!");
        }

        if (request.getPhone() != null && individualRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại này đã được một tài khoản Cá nhân khác sử dụng!");
        }

        // 2. Map DTO sang Entity
        Individual individual = new Individual();
        individual.setUsername(request.getUsername());
        individual.setPassword(passwordEncoder.encode(request.getPassword()));
        individual.setEmail(request.getEmail());
        individual.setPhone(request.getPhone());
        individual.setFullName(request.getFullName());
        individual.setAddress(request.getAddress());

        individual.setStatus(1);
        individual.setCreatedAt(LocalDateTime.now());

        individualRepository.save(individual);

        return individual.getId();
    }

    public String registerOrganization(RegisterOrgRequest request) {
        // 1. Kiểm tra tồn tại
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng!");
        }

        if (request.getPhone() != null && organizationRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại này đã được một Tổ chức khác sử dụng!");
        }

        // 2. Map DTO sang Entity
        Organization organization = new Organization();
        organization.setUsername(request.getUsername());
        organization.setPassword(passwordEncoder.encode(request.getPassword()));
        organization.setName(request.getName());
        organization.setDoe(request.getDoe());
        organization.setWebsiteURL(request.getWebsiteURL());
        organization.setDescription(request.getDescription());
        organization.setEmail(request.getEmail());
        organization.setPhone(request.getPhone());
        organization.setAddress(request.getAddress());

        organization.setStatus(2);
        organization.setCreatedAt(LocalDateTime.now());

        organizationRepository.save(organization);

        return organization.getId();
    }

    // Logic Đăng nhập & Tạo Token
    public String authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return jwtTokenProvider.generateToken(authentication);
    }
}