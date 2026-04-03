package group3.project.charityweb.security;

import group3.project.charityweb.model.entity.Account;
import group3.project.charityweb.model.entity.Admin;
import group3.project.charityweb.model.entity.Individual;
import group3.project.charityweb.model.entity.Organization;
import group3.project.charityweb.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm Account trong database (nhờ InheritanceType.JOINED, Hibernate sẽ tự động join các bảng con)
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));

        // 2. Kiểm tra trạng thái tài khoản (tùy chọn nhưng rất quan trọng)
        // Nếu status = 0 (Banned) hoặc 2 (Pending - chờ duyệt), ta có thể ném Exception chặn luôn đăng nhập
        if (account.getStatus() == 0) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        } else if (account.getStatus() == 2) {
            throw new RuntimeException("Tài khoản đang chờ Admin phê duyệt!");
        }

        // 3. Phân loại Role dựa trên thực thể con
        String roleName = "ROLE_USER"; // Fallback an toàn

        switch (account) {
            case Admin admin -> roleName = "ROLE_ADMIN";
            case Organization organization -> roleName = "ROLE_ORGANIZATION";
            case Individual individual -> roleName = "ROLE_INDIVIDUAL";
            default -> {
            }
        }

        // Tạo đối tượng Quyền cho Spring Security
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // 4. Trả về đối tượng User chuẩn của Spring Security (bọc username, password mã hóa và list quyền)
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                Collections.singletonList(authority)
        );
    }
}