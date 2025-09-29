package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    private final String SECRET = "supersecretkeysupersecretkeysupersecretkey";
    private final long EXPIRATION = 1000 * 60 * 60; // 1 hora

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw new RuntimeException("Token inválido", e);
        }
    }
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.before(new Date());
        } catch (JwtException e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    public Date getExpirationDate(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
        } catch (JwtException e) {
            log.error("Error getting expiration date: {}", e.getMessage());
            throw new RuntimeException("Token inválido", e);
        }
    }
    @Deprecated
    public String extractUsername(String token, String unused) {
        return extractUsername(token);
    }

    @Deprecated
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }
}
