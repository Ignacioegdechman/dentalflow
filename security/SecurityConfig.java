package com.dentalflow.dentalflow.security;

import com.dentalflow.dentalflow.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/webhooks/whatsapp/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/integraciones/**").hasAnyRole("ADMIN", "ODONTOLOGO", "RECEPCIONISTA")
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "ODONTOLOGO", "RECEPCIONISTA")
                        .requestMatchers("/reportes/**").hasAnyRole("ADMIN", "ODONTOLOGO", "RECEPCIONISTA")
                        .requestMatchers("/recordatorios/**").hasAnyRole("ADMIN", "ODONTOLOGO", "RECEPCIONISTA")
                        .requestMatchers("/pacientes/**").hasAnyRole("ADMIN", "ODONTOLOGO", "RECEPCIONISTA")
                        .requestMatchers("/turnos/**").hasAnyRole("ADMIN", "ODONTOLOGO", "RECEPCIONISTA")
                        .requestMatchers("/pagos/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers("/historia-clinica/**").hasAnyRole("ADMIN", "ODONTOLOGO")
                        .requestMatchers("/tratamientos/**").hasAnyRole("ADMIN", "ODONTOLOGO")
                        .requestMatchers("/odontologos/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByUsername(username)
                .map(usuario -> User.withUsername(usuario.getUsername())
                        .password(usuario.getPassword())
                        .roles(usuario.getRol() != null ? usuario.getRol().name() : "RECEPCIONISTA")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}