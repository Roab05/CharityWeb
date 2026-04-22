package group3.project.charityweb.config;

import group3.project.charityweb.model.entity.Admin;
import group3.project.charityweb.model.entity.ProjectCategory;
import group3.project.charityweb.model.enums.AccountStatus;
import group3.project.charityweb.repository.AccountRepository;
import group3.project.charityweb.repository.ProjectCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
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

            if (categoryRepository.count() == 0) {
                categoryRepository.save(new ProjectCategory("education", "Giáo dục", "Hỗ trợ học tập, trường lớp và học bổng", null));
                categoryRepository.save(new ProjectCategory("health", "Y tế", "Chi phí khám chữa bệnh và chăm sóc sức khỏe", null));
                categoryRepository.save(new ProjectCategory("environment", "Môi trường", "Hoạt động bảo vệ môi trường và phát triển bền vững", null));
                categoryRepository.save(new ProjectCategory("society", "Xã hội", "Hỗ trợ cộng đồng và an sinh xã hội", null));
                categoryRepository.save(new ProjectCategory("animal", "Động vật", "Cứu trợ và bảo vệ động vật", null));
                categoryRepository.save(new ProjectCategory("housing", "Nhà ở", "Xây dựng và sửa chữa nhà cho người khó khăn", null));
                categoryRepository.save(new ProjectCategory("technology", "Công nghệ", "Dự án công nghệ phục vụ cộng đồng", null));
                categoryRepository.save(new ProjectCategory("other", "Khác", "Các dự án thuộc lĩnh vực khác", null));

                System.out.println("✅ Đã khởi tạo danh mục dự án mặc định!");
            }
        };
    }
}