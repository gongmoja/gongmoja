package com.est.gongmoja.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                //메인 페이지는 전부 접근 가능
                                .requestMatchers("/").permitAll()
                                //로그인 페이지 , 회원가입 페이지는 전부 접근 가능
                                .requestMatchers("/login","/register").permitAll()
                                //이외 페이지는 인가된 이용자만 접근 가능
                                .anyRequest().authenticated()
                                //추후 비회원의 접근이 어디까지가 괜찮을지 논의
//                                .anyRequest().permitAll()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }
}
