package group3.project.charityweb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // spring's web security support
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // disable CSRF for building a stateless API (not using cookies for sessions)
                .csrf(AbstractHttpConfigurer::disable)

                // define authorization rules, allow public access to all endpoints
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                // set Session Management to stateless
                // (not to create sessions, ideal for REST APIs)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}

/*
// 2. Define Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to the registration and login endpoints
                        // IMPORTANT: Replace with your actual controller paths
                        .requestMatchers("/api/donors/register", "/api/donors/login").permitAll()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
 */