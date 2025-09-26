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
                .csrf(csrf -> csrf.disable()) // 🔹 Deshabilitar CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // 🔹 Permitir TODO
                )
                .formLogin(form -> form.disable()) // 🔹 Quitar login por formulario
                .httpBasic(basic -> basic.disable()); // 🔹 Quitar Basic Auth

        return http.build();
    }
}
