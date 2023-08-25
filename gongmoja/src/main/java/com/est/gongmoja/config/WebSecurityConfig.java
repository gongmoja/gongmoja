package com.est.gongmoja.config;

import com.est.gongmoja.jwt.JwtTokenFilter;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final CustomLogoutHandler customLogoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
//                                //메인 페이지는 전부 접근 가능
//                                .requestMatchers("/").permitAll()
//                                //oauth2 관련 익명 접근 가능
//                                .requestMatchers("/oauth2/**").anonymous()
//                                //로그인 페이지 , 회원가입 페이지는 익명 접근 가능
//                                .requestMatchers("/login","/register").anonymous()
//                                //이외 페이지는 인가된 이용자만 접근 가능
//                                .anyRequest().authenticated()
                                //추후 비회원의 접근이 어디까지가 괜찮을지 논의
                               .anyRequest().permitAll()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
}
