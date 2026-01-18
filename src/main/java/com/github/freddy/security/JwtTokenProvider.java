package com.github.freddy.security;

import com.github.freddy.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${jwt.access-token.expiration}")
    private long expirationTime;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // --- Geração de Tokens ---

    public String generateAccessToken(UserDetails userDetails) {
        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = (User) userDetails;

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("id", user.getId().toString())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key)
                .compact();
    }

    // --- Métodos de Extração Pública ---

    public String getUserEmailFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getUserIdFromToken(String token) {
        return getClaim(token, claims -> claims.get("id", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return getClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    // --- Validação ---

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = getUserEmailFromToken(token);
            // Verifica se o email do token coincide com o usuário do banco e se não expirou
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // --- Métodos Auxiliares e Privados (O "Coração" do Provider) ---

    /**
     * Método genérico para extrair qualquer informação (Claim) do Token.
     */
    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Faz o parse do Token, valida a assinatura com a nossa chave secreta
     * e retorna o corpo (Payload) com todas as informações.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key) // Usa a chave preparada no @PostConstruct
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
