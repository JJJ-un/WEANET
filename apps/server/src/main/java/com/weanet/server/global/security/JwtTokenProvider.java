package com.weanet.server.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret:vmfhaltmskdlstkfkdgodyrhtnavmffhqkfrhradevex}") String secretKey,
            @Value("${jwt.expiration:3600000}") long validityInMilliseconds) {
        // 256비트 이상의 키가 필요하므로 충분히 긴 기본값을 설정했습니다.
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    /**
     * 토큰 생성: 사용자의 이메일을 기반으로 JWT 토큰을 만듭니다.
     */
    public String createToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /**
     * 정보 추출: 토큰에서 사용자의 이메일(Subject)을 꺼냅니다.
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 유효성 검사: 토큰이 변조되지 않았는지, 만료되지 않았는지 확인합니다.
     */
    public boolean validateToken(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
