package com.est.gongmoja.service;

import com.est.gongmoja.dto.user.UserLoginRequestDto;
import com.est.gongmoja.dto.user.UserLoginResponseDto;
import com.est.gongmoja.entity.Role;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
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
    private final RedisService redisService;

    //회원가입 로직
    public void createUser(UserRegisterRequestDto requestDto) {
        //userName 중복 예외처리
        Optional<UserEntity> optionalUser = userRepository.findByUserName(requestDto.getUsername());
        if (optionalUser.isPresent()) throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);

        //유저 객체 저장
        userRepository.save(UserEntity
                .builder()
                .userName(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword())) //암호화하여 저장
                .email(requestDto.getEmail())
                .role(Role.ROLE_USER)
                .nickName(requestDto.getNickname())
                .build());
    }

    //로그인 로직 ( accessToken 발급, refreshToken 발급 )
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        //userName 또는 password 불일치 예외처리 로직
        Optional<UserEntity> optionalUser = userRepository.findByUserName(requestDto.getUsername());
        if (optionalUser.isEmpty()) throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        else if (!passwordEncoder.matches(requestDto.getPassword(), optionalUser.get().getPassword()))
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);

        //유저 객체 생성
        UserEntity userEntity = optionalUser.get();

        //token 생성 로직 ( accessToken 과 refreshToken 동시 생성 )
        String accessToken = jwtTokenUtil.createToken(userEntity.getUserName(), JwtTokenUtil.accessTokenExpireMs);
        String refreshToken = jwtTokenUtil.createToken(userEntity.getUserName(), JwtTokenUtil.refreshTokenExpireMs);

//        //refreshToken 은 DB 에 저장
//        refreshTokenRepository.save(RefreshTokenEntity
//                .builder()
//                .userName(userEntity.getUserName())
//                .refreshToken(refreshToken)
//                .build());

        //refreshToken 을 Redis 에 저장
        //key : username , value : refreshToken
        redisService.setData(userEntity.getUserName(),refreshToken);

        //token 실어서 return
        return UserLoginResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //로그아웃 로직 ( refreshToken 삭제 )
    public void logOut(String username) {
//        // refreshToken 삭제
//        refreshTokenRepository.deleteById(username);

        //Redis 에서 삭제
        redisService.deleteData(username);
    }

    public UserEntity getUser(String username){
        return userRepository.findByUserName(username).orElseThrow(()->new CustomException(ErrorCode.USERNAME_NOT_FOUND));
    }

    public void saveUser(UserEntity user){
        userRepository.save(user);
    }


    public void modifyPassword(String newPassword ,UserEntity userEntity){
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    public boolean checkPassword(String nowPassword,UserEntity userEntity){
        return passwordEncoder.matches(nowPassword,userEntity.getPassword());
    }

}