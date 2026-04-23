package group3.project.charityweb.config;

import group3.project.charityweb.model.entity.Admin;
import group3.project.charityweb.model.enums.AccountStatus;
import group3.project.charityweb.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(AccountRepository accountRepository,
                                      PasswordEncoder passwordEncoder) {
        return _ -> {
            if (!accountRepository.existsByUsername("admin")) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setStatus(AccountStatus.ACTIVE); // 1 = Active
                admin.setCreatedAt(LocalDateTime.now());
                admin.setAdminLevel("SUPER_ADMIN");

                accountRepository.save(admin);

                System.out.println("✅ Đã khởi tạo tài khoản Admin mặc định thành công!");
            }
        };
    }
}