package com.kwh.kwhapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/ping", "/api/consumo/calcular", "/api/consumo/historial").permitAll() // estas rutas serán públicas
        .anyRequest().authenticated()             // el resto estará protegido
      );
    return http.build();
  }
}