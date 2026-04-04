package group3.project.charityweb.config;

import group3.project.charityweb.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC APIs
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/activities/**").permitAll()
                        .requestMatchers("/api/v1/payments/callback").permitAll()

                        // 2. ADMIN APIs
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")

                        // 3. ORGANIZATION APIs
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects").hasAuthority("ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/disbursements").hasAuthority("ROLE_ORGANIZATION")

                        // 4. Các API còn lại yêu cầu đăng nhập
                        .anyRequest().authenticated()
                );

        // Filter được inject thẳng từ tham số hàm vào đây
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}