package com.example.Client.config;

import com.example.Client.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к Swagger UI и OpenAPI без аутентификации
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Разрешаем доступ к эндпоинтам аутентификации и регистрации
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/register").permitAll()
                        // Ограничиваем доступ по ролям
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/publisher/**").hasRole("PUBLISHER")
                        .requestMatchers("/api/playlists/create", "/api/playlists/*/edit").hasAnyRole("ADMIN", "PUBLISHER")
                        .requestMatchers("/api/audio-files/upload").hasAnyRole("ADMIN", "PUBLISHER")
                        .requestMatchers("/api/audio-files/*/delete").hasAnyRole("ADMIN", "PUBLISHER")
                        .requestMatchers("/api/playlists/*/delete").hasAnyRole("ADMIN", "PUBLISHER")
                        .requestMatchers("/api/playlists/favorite/**").hasAnyRole("USER", "ADMIN", "PUBLISHER")
                        .requestMatchers("/api/audio-files/*/play").authenticated()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/audio-files/**").permitAll()
                        // Разрешаем доступ к странице ошибок
                        .requestMatchers("/error").permitAll()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}