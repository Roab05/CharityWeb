package group3.project.charityweb.config;

import group3.project.charityweb.security.CustomAccessDeniedHandler;
import group3.project.charityweb.security.JwtAuthenticationEntryPoint;
import group3.project.charityweb.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC APIs
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/activities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/donations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/projects/*/disbursements").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/activities/*/interactions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/callback").permitAll()

                        // 2. ADMIN APIs
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")

                        // 3. ORGANIZATION APIs
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects").hasAuthority("ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/projects").hasAuthority("ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/activities").hasAuthority("ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/activities/*/interactions")
                        .hasAnyAuthority("ROLE_INDIVIDUAL", "ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/interactions/*")
                        .hasAnyAuthority("ROLE_INDIVIDUAL", "ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/activities/**").hasAuthority("ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/activities/**").hasAuthority("ROLE_ORGANIZATION")
                        .requestMatchers(HttpMethod.POST, "/api/v1/projects/*/disbursements").hasAuthority("ROLE_ORGANIZATION")

                        // 4. Các API còn lại yêu cầu đăng nhập
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        configuration.setAllowCredentials(true);

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Accept"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}