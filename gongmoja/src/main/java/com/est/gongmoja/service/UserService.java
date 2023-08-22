package com.est.gongmoja.service;

import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.est.gongmoja.dto.user.UserRegisterRequestDto;
import com.est.gongmoja.jwt.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    //회원가입 로직
    public void createUser(UserRegisterRequestDto requestDto){

    }

    //로그인 로직 ( accessToken 발급, refreshToken 발급 )
    public void dd(){

    }

    //로그아웃 로직 ( accessToken 삭제 )




}
