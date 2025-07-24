package com.tms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/submit/**").authenticated()
                .anyRequest().permitAll()
            )
            // ✅ Replace deprecated frameOptions() with direct header config
            .headers(headers -> {
                headers.disable(); // disables all headers including frameOptions
            })
            // ✅ Replace Customizer.withDefaults() with a lambda
            .httpBasic(httpBasic -> {});

        return http.build();
    }
}
