package com.est.gongmoja.jwt;

import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //cookie 가 없다면
        if(request.getCookies() == null){
            log.info("쿠키 없음");
            filterChain.doFilter(request,response);
            return;
        }

        //accessToken 세팅
        Cookie accessTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName()
                        .equals("gongMoAccessToken"))
                .findAny()
                .orElse(new Cookie("noCookie","noCookie"));
        log.info(accessTokenCookie.getName());

        //refreshToken 세팅
        Cookie refreshTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName()
                        .equals("gongMoRefreshToken"))
                .findAny()
                .orElse(new Cookie("noCookie","noCookie"));
        log.info(accessTokenCookie.getName());

        //accessToken 이 들어있는 cookie 가 없다면?
        if(accessTokenCookie.getName().equals("noCookie")){
            log.info("access 쿠키 없음");
            filterChain.doFilter(request,response);
        }
        filterChain.doFilter(request,response);
        //accessToken, refreshToken 쿠키 파싱
        String accessToken = accessTokenCookie.getValue();
        String refreshToken = refreshTokenCookie.getValue();

        //만약 토큰이 expired 라면?
        if(jwtTokenUtil.isValidToken(accessToken).equals("expired")){
            //todo refresh 토큰

        }
        //만약 valid 하지 않은 토큰이라면?
        else if(jwtTokenUtil.isValidToken(accessToken).equals("notValid")){
            //todo

        }
        //만약 정상적인 토큰이라면? > 컨텍스트 작성
        else if(jwtTokenUtil.isValidToken(accessToken).equals("ok")){

        }


    }
}
