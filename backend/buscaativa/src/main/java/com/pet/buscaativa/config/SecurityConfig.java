package com.pet.buscaativa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityFilter securityFilter;

    /*
     * Segurança exclusiva do H2 Console
     */

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher(PathRequest.toH2Console())
                .csrf(csrf -> csrf.disable())
                .headers(headers ->
                        headers.frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()
                );

        return http.build();
    }

    /*
     * Segurança da API
     */
    @Bean
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()

                        // Login
                        .requestMatchers("/api/auth/login").permitAll()

                        // Permite requisições OPTIONS (CORS)
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // H2
                        .requestMatchers("/h2-console/**").permitAll()

                        // Rota de erro interna do Spring (evita 403 mascarando outros erros, como 400/500)
                        .requestMatchers("/error").permitAll()

                        // Restante protegido
                        .anyRequest().authenticated()
                )

                // Filtro JWT
                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {

        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}