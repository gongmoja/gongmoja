package com.est.gongmoja.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private final Key signingKey;
    private final JwtParser jwtParser;
    public JwtTokenUtil(@Value("${jwt.secret}")String secret){
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
    }

    public static long accessTokenExpireMs = 1000 * 60 * 30; // accessToken 유효기간 30분
    public static long refreshTokenExpireMs = 1000 * 60 * 60 * 12; // refreshToken 유효기간 12시간

    // JWT 발급
    public String createToken(String username , Long expireTime){
        // accessToken 의 Claim 생성. expireTime 파라미터에
        Claims claims = Jwts
                .claims()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime));

        return Jwts.builder().signWith(signingKey).setClaims(claims).compact();
    }

    // JWT 유효성 검증
    public String isValidToken(String username){
        try {
            jwtParser.parseClaimsJws(username);
            return "ok";
        } catch (ExpiredJwtException e){ // 토큰 만료 예외처리
            return "expired";
        } catch (Exception e){ // 토큰 유효하지 않음 예외처리
            return "notValid";
        }
    }

    // JWT 안에 있는 username 추출 ( 파싱 )
    public String getUsername(String token){
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }
}
