package com.github.freddy.security;

import com.github.freddy.dtos.errors.StandardError;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // Jackson para converter Objeto em JSON

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, java.io.IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Usando o seu DTO StandardError para manter o padrão
        StandardError err = new StandardError(
                Instant.now(),
                HttpServletResponse.SC_UNAUTHORIZED,
                "Credenciais inválidas ou token ausente.",
                request.getRequestURI()
        );

        // O writeValue escreve o JSON direto no corpo da resposta
        response.getWriter().write(objectMapper.writeValueAsString(err));
    }
}
