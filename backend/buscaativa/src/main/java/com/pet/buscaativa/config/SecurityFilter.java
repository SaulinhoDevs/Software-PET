package com.pet.buscaativa.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pet.buscaativa.services.TokenService;
import com.pet.buscaativa.services.impl.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recuperarToken(request);
        System.out.println(">>> URI: " + request.getRequestURI());
        System.out.println(">>> Token recebido: " + token);

        if (token != null) {
            var email = tokenService.validarToken(token);
            System.out.println(">>> Email retornado pelo validarToken: '" + email + "'");

            if (!email.isEmpty()) {
                UserDetails usuario = userDetailsService.loadUserByUsername(email);
                System.out.println(">>> Authorities do usuário: " + usuario.getAuthorities());

                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println(">>> Autenticação setada no contexto com sucesso.");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        return authHeader.replace("Bearer ", "");
    }
}