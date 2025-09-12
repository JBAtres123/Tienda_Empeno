package com.online.tienda_empeno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Solo para pruebas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login/**").permitAll() // Permitir login sin auth
                        .anyRequest().authenticated() // Todo lo demás protegido
                )
                .httpBasic(httpBasic -> {}); // HTTP Basic para otros endpoints

        return http.build();
    }
}
