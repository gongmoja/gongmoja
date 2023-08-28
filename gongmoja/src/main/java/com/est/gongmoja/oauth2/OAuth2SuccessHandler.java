package com.est.gongmoja.oauth2;

import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.jwt.CookieUtil;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.repository.RefreshTokenRepository;
import com.est.gongmoja.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //OAuth 2 인증에 성공했을 경우 여기로 들어온다
        log.info("success 안에 들어옴");
        //String targetUrl = determineTargetUrl(request, response, authentication);

        //auth 에 담긴 유저정보 담은 객체 생성
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = jwtTokenUtil.createToken(customOAuth2User.getEmail(),JwtTokenUtil.accessTokenExpireMs);
        String refreshToken = jwtTokenUtil.createToken(customOAuth2User.getEmail(),JwtTokenUtil.refreshTokenExpireMs);
        CookieUtil.addCookie(response,"gongMoAccessToken",accessToken, (int) ((JwtTokenUtil.refreshTokenExpireMs/1000) + 10));
        CookieUtil.addCookie(response,"gongMoRefreshToken",refreshToken, (int) ((JwtTokenUtil.refreshTokenExpireMs/1000) + 10));




        getRedirectStrategy().sendRedirect(request, response,"/");
    }

//    private void redirect(HttpServletRequest request, HttpServletResponse response, String email) throws IOException {
//        log.info("Token 생성 시작");
//        // User 객체 생성
//        UserEntity userEntity = userRepository.findByUserName(email).orElseThrow(()->new CustomException(ErrorCode.USERNAME_NOT_FOUND));
//
//        // Access Token 생성
//        String accessToken = jwtTokenUtil.createToken(userEntity.getUserName(),JwtTokenUtil.accessTokenExpireMs);
//        // Refresh Token 생성
//        String refreshToken = jwtTokenUtil.createToken(userEntity.getUserName(),JwtTokenUtil.refreshTokenExpireMs);
//
//
//        //Return 할 URI 생성
//        String uri = createURI(accessToken, refreshToken, userId, username).toString();   // Access Token과 Refresh Token을 포함한 URL을 생성
//        getRedirectStrategy().sendRedirect(request, response, uri);   // sendRedirect() 메서드를 이용해 Frontend 애플리케이션 쪽으로 리다이렉트
//    }
//
//    // Redirect URI 생성. JWT를 쿼리 파라미터로 담아 전달
//    private URI createURI(String accessToken, String refreshToken, Long userId, String username) {
//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("user_id", String.valueOf(userId));
//        queryParams.add("username", username);
//        queryParams.add("access_token", accessToken);
//        queryParams.add("refresh_token", refreshToken);
//
//        return UriComponentsBuilder
//                .newInstance()
//                .scheme("http")
//                .host("localhost")
//                .path("/oauth")
//                .queryParams(queryParams)
//                .build()
//                .toUri();
//    }
}
