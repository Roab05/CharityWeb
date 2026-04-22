package group3.project.charityweb.service.impl;

import group3.project.charityweb.exception.DuplicateResourceException;
import group3.project.charityweb.model.dto.request.LoginRequest;
import group3.project.charityweb.model.dto.request.RegisterIndivRequest;
import group3.project.charityweb.model.dto.request.RegisterOrgRequest;
import group3.project.charityweb.model.entity.Individual;
import group3.project.charityweb.model.entity.Organization;
import group3.project.charityweb.model.enums.AccountStatus;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.IndividualRepository;
import group3.project.charityweb.repository.OrganizationRepository;
import group3.project.charityweb.repository.UserRepository;
import group3.project.charityweb.security.JwtTokenProvider;
import group3.project.charityweb.service.AuthService;
import group3.project.charityweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

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
    private final UserService userService;

    @Override
    public String registerIndividual(RegisterIndivRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng!");
        }

        if (request.getPhone() != null && individualRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại này đã được một tài khoản Cá nhân khác sử dụng!");
        }

        Individual individual = new Individual();
        individual.setUsername(request.getUsername());
        individual.setPassword(passwordEncoder.encode(request.getPassword()));
        individual.setEmail(request.getEmail());
        individual.setPhone(request.getPhone());
        individual.setFullName(request.getFullName());
        individual.setAddress(request.getAddress());

        individual.setStatus(AccountStatus.ACTIVE);
        individual.setCreatedAt(LocalDateTime.now());

        individualRepository.save(individual);

        return individual.getId();
    }

    @Override
    public String registerOrganization(RegisterOrgRequest request) {
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã được sử dụng!");
        }

        if (request.getPhone() != null && organizationRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại này đã được một Tổ chức khác sử dụng!");
        }

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

        organization.setStatus(AccountStatus.PENDING);
        organization.setCreatedAt(LocalDateTime.now());

        organizationRepository.save(organization);

        return organization.getId();
    }

    @Override
    public Map<String, Object> authenticate(LoginRequest request) {
        String normalizedUsername = request.getUsername() == null ? null : request.getUsername().trim();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedUsername, request.getPassword())
        );

        // 2. Sinh ra cặp Token
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        String authenticatedUsername = authentication.getName();
        Object userProfile = userService.getMyProfile(authenticatedUsername);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "userProfile", userProfile
        );
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Refresh Token không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại.");
        }

        String username = jwtTokenProvider.getUsernameFromJwtToken(refreshToken);

        return jwtTokenProvider.generateAccessTokenFromUsername(username);
    }
}