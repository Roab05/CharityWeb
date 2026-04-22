package group3.project.charityweb.security;

import group3.project.charityweb.model.entity.Account;
import group3.project.charityweb.model.entity.Admin;
import group3.project.charityweb.model.entity.Individual;
import group3.project.charityweb.model.entity.Organization;
import group3.project.charityweb.model.enums.AccountStatus;
import group3.project.charityweb.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));

        if (account.getStatus() == AccountStatus.BANNED) {
            throw new LockedException("Tài khoản của bạn đã bị khóa!");
        } else if (account.getStatus() == AccountStatus.PENDING) {
            throw new DisabledException("Tài khoản đang chờ Admin phê duyệt!");
        }

        String roleName = "ROLE_USER";

        switch (account) {
            case Admin ignored -> roleName = "ROLE_ADMIN";
            case Organization ignored -> roleName = "ROLE_ORGANIZATION";
            case Individual ignored -> roleName = "ROLE_INDIVIDUAL";
            default -> {
            }
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                Collections.singletonList(authority)
        );
    }
}