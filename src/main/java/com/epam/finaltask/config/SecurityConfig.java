package com.epam.finaltask.config;

import com.epam.finaltask.token.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Открываем доступ к Swagger UI и документации API
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Открываем доступ к публичным HTML-страницам и логауту
                        .requestMatchers("/", "/auth/sign-in", "/auth/sign-up", "/auth/logout", "/error").permitAll()

                        // Открываем доступ к статике
                        .requestMatchers("/css/**", "/js/**", "/webjars/**").permitAll()

                        // Открываем доступ к REST эндпоинтам авторизации
                        .requestMatchers("/api/users/register", "/api/users/login", "/login", "/register").permitAll()

                        // Добавляем защиту админской и менеджерской зоны
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/manager/**").hasAnyAuthority("MANAGER", "ADMIN")

                        // Явно разрешаем профиль и заказ тура только для авторизованных
                        .requestMatchers("/profile/**", "/vouchers/order").authenticated()

                        // Все остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}