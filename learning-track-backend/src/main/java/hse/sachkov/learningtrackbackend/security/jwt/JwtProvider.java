package hse.sachkov.learningtrackbackend.security.jwt;

import hse.sachkov.learningtrackbackend.security.applicationuser.ApplicationUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Component
public class JwtProvider {

    private final int tokenExpirationAfterDays;
    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public JwtProvider(
            @Value("${application.jwt.tokenExpirationAfterDays}") int tokenExpirationAfterDays,
            @Value("${application.jwt.accessSecret}") String accessSecret,
            @Value("${application.jwt.refreshSecret}") String refreshSecret) {
        this.tokenExpirationAfterDays = tokenExpirationAfterDays;
        this.accessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        this.refreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateAccessToken(@NonNull ApplicationUser applicationUser) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .claim("authorities", applicationUser.getAuthorities())
                .setSubject(applicationUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(LocalDateTime.now().plusDays(tokenExpirationAfterDays)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(accessSecret)
                .compact();
    }

    public String generateRefreshToken(@NonNull ApplicationUser applicationUser) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .claim("authorities", applicationUser.getAuthorities())
                .setSubject(applicationUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(LocalDateTime.now().plusDays(tokenExpirationAfterDays + 100)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(accessSecret)
                .compact();
    }

    private boolean validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired!", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt!", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt!", mjEx);
        } catch (Exception e) {
            log.error("Invalid token!", e);
        }

        return false;
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, accessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, refreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, accessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, refreshSecret);
    }
}
