package com.est.gongmoja.jwt;

import com.est.gongmoja.entity.RefreshTokenEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.RefreshTokenRepository;
import com.est.gongmoja.repository.UserRepository;
import com.est.gongmoja.service.RedisService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RedisService redisService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {



        //cookie 가 없다면
        if(request.getCookies() == null){
            filterChain.doFilter(request,response);
            return;
        }

        //accessToken 세팅
        Optional<Cookie> accessTokenCookie = CookieUtil.getCookie(request,"gongMoAccessToken");
        //refreshToken 세팅
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookie(request,"gongMoRefreshToken");



        //case 1: refreshToken 은 살아있으나 , accessToken 이 없어진 경우
        if(accessTokenCookie.isEmpty() && refreshTokenCookie.isPresent()){
//            log.info("refreshToken 검증 시작");
            String refreshToken = refreshTokenCookie.get().getValue();
            String username = jwtTokenUtil.getUsername(refreshToken);
            String refreshTokenFromRedis = redisService.getData(username);
            if(!refreshToken.equals(refreshTokenFromRedis)) throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);

//            log.info("AccessToken 재생성 시작");
            String accessToken = jwtTokenUtil.createToken(username,JwtTokenUtil.accessTokenExpireMs);
            CookieUtil.addCookie(response,"gongMoAccessToken",accessToken,(int)((JwtTokenUtil.accessTokenExpireMs/1000)));
            createContext(accessToken);
            filterChain.doFilter(request,response);
            return;
        }

        //case 2: 토큰 둘다 없는 경우
        if(refreshTokenCookie.isEmpty() || accessTokenCookie.isEmpty()){
//            log.info("Token 없음");
            filterChain.doFilter(request,response);
            return;
        }




        //accessToken, refreshToken 쿠키 파싱
        String accessToken = accessTokenCookie.get().getValue();
        String refreshToken = refreshTokenCookie.get().getValue();

        //토큰의 상태 객체 ( ok , expired , invalid )
        String atStatus = jwtTokenUtil.isValidToken(accessToken);
        String rtStatus = jwtTokenUtil.isValidToken(refreshToken);

        //만약 accessToken 이 expired 라면?
        if(atStatus.equals(ErrorCode.TOKEN_EXPIRED.name())){

//            log.info("accessToken 만료");

            //만약 refreshToken 도 expired 라면?
            if(rtStatus.equals(ErrorCode.TOKEN_EXPIRED.name())){
//                log.info("refreshToken 만료. 다시 로그인이 필요");
                throw new CustomException(ErrorCode.RE_LOGIN_REQUIRED);
//                filterChain.doFilter(request,response);
            }

            else if(rtStatus.equals(ErrorCode.TOKEN_INVALID.name())){
//                log.info("refreshToken INVALID");
                filterChain.doFilter(request,response);
            }

            //refreshToken 이 유효하다면?
            else{

                //쿠키 refreshToken 과 DB refreshToken 대조
                String username = jwtTokenUtil.getUsername(refreshToken);

//                Optional<RefreshTokenEntity> optionalToken = refreshTokenRepository.findById(username);
                String optionalToken = redisService.getData(username);
                if(optionalToken == null){
//                    log.info("서버 내의 리프레시 토큰과 일치하지 않음");
//                    filterChain.doFilter(request,response);
                    throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
                }

//                log.info("accessToken 재발급");

                //새로운 accessToken 생성
                String newAccessToken = jwtTokenUtil.createToken(username,JwtTokenUtil.accessTokenExpireMs);

                //쿠키 객체에 담아 응답쿠키에 보냄
                CookieUtil.addCookie(
                        response,
                        "gongMoAccessToken",
                        newAccessToken,
                        (int)((JwtTokenUtil.accessTokenExpireMs/1000)));

                createContext(newAccessToken);
//                log.info("인증됨");
                filterChain.doFilter(request,response);
            }
        }
        //만약 valid 하지 않은 토큰이라면?
        else if(atStatus.equals(ErrorCode.TOKEN_INVALID.name())){
//            log.info("비정상 토큰");
            filterChain.doFilter(request,response);
        }
        //만약 정상적인 토큰이라면? > 컨텍스트 작성
        else if(atStatus.equals("ok")){
            createContext(accessToken);
            filterChain.doFilter(request,response);
        }
    }

    //인증정보 생성 메서드
    private void createContext(String accessToken){
        String username = jwtTokenUtil.getUsername(accessToken);
        UserEntity userEntity = userRepository.findByUserName(username).get();

//        log.info(username);
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                UserEntity
                        .builder()
                        .userName(username)
                        .build(),
                accessToken,
                List.of(new SimpleGrantedAuthority(userEntity.getRole().name())));

        //인증정보 저장
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
//        log.info("인증됨");
    }
}
