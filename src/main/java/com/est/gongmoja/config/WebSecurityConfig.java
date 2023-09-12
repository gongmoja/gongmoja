package com.est.gongmoja.config;

import com.est.gongmoja.entity.Role;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.jwt.JwtTokenFilter;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.oauth2.CustomOAuth2UserService;
import com.est.gongmoja.oauth2.OAuth2SuccessHandler;
import com.est.gongmoja.repository.UserRepository;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final CustomLogoutHandler customLogoutHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
//                                //정적 파일 모두 접근 가
                                .requestMatchers("/static/**").permitAll()
                                .requestMatchers("/calendar/**").permitAll()
                                .requestMatchers("/calendar").permitAll()
                                //메인 페이지는 전부 접근 가능
                                .requestMatchers("/").permitAll()
                                //oauth2 관련 익명 접근 가능
                                .requestMatchers("/oauth2/**").anonymous()
                                //로그인 페이지 , 회원가입 페이지는 익명 접근 가능
                                .requestMatchers("/login","/register").anonymous()
                                .requestMatchers("/forgot-password").anonymous()
                                //이외 페이지는 인가된 이용자만 접근 가능
                                .anyRequest().authenticated()
//                                //추후 비회원의 접근이 어디까지가 괜찮을지 논의
//                               .anyRequest().permitAll()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/login")
                                .successHandler(oAuth2SuccessHandler)
                                .userInfoEndpoint(
                                        userInfo->userInfo
                                                .userService(customOAuth2UserService)
                                )
                )
                .logout(logoutConfigurerCustomizer())
                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class)
                .build();
    }

    @Bean
    public Customizer<LogoutConfigurer<HttpSecurity>> logoutConfigurerCustomizer(){
        return logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutHandler);
    }

    @Bean
    public void makeAdmin(){

        Optional<UserEntity> optional = userRepository.findByUserName("ADMIN0000");
        if(optional.isPresent()){
            return;
        }
        userRepository.save(UserEntity
                .builder()
                .userName("ADMIN0000")
                .password(passwordEncoder.encode("1234"))
                .nickName("관리자")
                .role(Role.ROLE_ADMIN)
                .build());
    }
}
