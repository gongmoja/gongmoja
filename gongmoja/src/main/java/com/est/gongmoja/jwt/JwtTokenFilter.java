package com.est.gongmoja.jwt;

import com.est.gongmoja.entity.RefreshTokenEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.RefreshTokenRepository;
import com.est.gongmoja.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
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


        //refreshToken 세팅
        Cookie refreshTokenCookie = Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName()
                        .equals("gongMoRefreshToken"))
                .findAny()
                .orElse(new Cookie("noCookie","noCookie"));

        //accessToken 이 들어있는 cookie 가 없다면?
        if(accessTokenCookie.getName().equals("noCookie")){
            log.info("access 쿠키 없음");
            filterChain.doFilter(request,response);
            return;
        }
        //밸류가 삭제되었다면? (로그아웃)
        else if(accessTokenCookie.getValue().equals("destroyed") || refreshTokenCookie.getValue().equals("destroyed")){
            log.info("삭제된 토큰");
            filterChain.doFilter(request,response);
            return;
        }

        //accessToken, refreshToken 쿠키 파싱
        String accessToken = accessTokenCookie.getValue();
        String refreshToken = refreshTokenCookie.getValue();

        //토큰의 상태 객체 ( ok , expired , invalid )
        String atStatus = jwtTokenUtil.isValidToken(accessToken);
        String rtStatus = jwtTokenUtil.isValidToken(refreshToken);

        //만약 accessToken 이 expired 라면?
        if(atStatus.equals(ErrorCode.TOKEN_EXPIRED.name())){
            log.info("만료된 토큰");

            //만약 refreshToken 도 expired 라면?
            if(rtStatus.equals(ErrorCode.TOKEN_EXPIRED.name())){
                log.info("리프레시 토큰의 유효시간이 지남. 다시 로그인이 필요함");
                filterChain.doFilter(request,response);
            }
            else if(rtStatus.equals(ErrorCode.TOKEN_INVALID.name())){
                log.info("잘못 된 리프레시 토큰");
                filterChain.doFilter(request,response);
            }
            //refreshToken 이 유효하다면?
            else{
                log.info("accessToken 재발급");
                //refreshToken 에서 username 정보 가져옴
                String username = jwtTokenUtil.getUsername(refreshToken);
                //새로운 accessToken 생성
                String newAccessToken = jwtTokenUtil.createToken(username,JwtTokenUtil.accessTokenExpireMs);
                //쿠키 객체에 담아 응답쿠키에 보냄
                Cookie newCookie = new Cookie("gongMoAccessToken",newAccessToken);
                response.addCookie(newCookie);

                //인증정보 생성
                //todo 인증정보 반복된 코드 메서드분리 해서 리팩터 하는것 고민
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        UserEntity.builder().userName(username).build(),
                        newAccessToken,
                        new ArrayList<>()
                );

                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                log.info("인증정보 재생성 완료");
                filterChain.doFilter(request,response);
            }
        }
        //만약 valid 하지 않은 토큰이라면?
        else if(atStatus.equals(ErrorCode.TOKEN_INVALID.name())){
            log.info("비정상 토큰");
            filterChain.doFilter(request,response);
        }
        //만약 정상적인 토큰이라면? > 컨텍스트 작성
        else if(atStatus.equals("ok")){
            String username = jwtTokenUtil.getUsername(accessToken);
            log.info(username);
            SecurityContext context = SecurityContextHolder.createEmptyContext();

            AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    UserEntity
                            .builder()
                            .userName(username)
                            .build(),
                    accessToken,
                    new ArrayList<>());

            //인증정보 저장
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
            log.info("인증정보 생성완료");
            filterChain.doFilter(request,response);
        }
    }
}
