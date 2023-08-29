package com.est.gongmoja.controller;

import com.est.gongmoja.dto.user.UserLoginRequestDto;
import com.est.gongmoja.dto.user.UserLoginResponseDto;
import com.est.gongmoja.dto.user.UserRegisterRequestDto;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.jwt.CookieUtil;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @GetMapping // 메인페이지 ( 추후 컨트롤러 다른 곳으로 이동해야함 임시로 )
    public String mainPage(){
        return "main";
    }

    @GetMapping("/login") // 로그인 페이지로 이동
    public String loginPage(Model model){
        //dto object 전달
        model.addAttribute("userLoginRequestDto",new UserLoginRequestDto());
        return "users/login";
    }

    @PostMapping("/login") // 로그인 요청
    public String loginRequest(
            @ModelAttribute UserLoginRequestDto requestDto,
            Model model,
            HttpServletResponse response
    ){
        try{
            //accessToken & refreshToken 가져옴
            UserLoginResponseDto loginResponseDto = userService.login(requestDto);


            //쿠키에 저장
            CookieUtil.addCookie(
                    response,
                    "gongMoAccessToken",
                    loginResponseDto.getAccessToken(),
                    (int) ((JwtTokenUtil.refreshTokenExpireMs/1000)));
            CookieUtil.addCookie(
                    response,
                    "gongMoRefreshToken",
                    loginResponseDto.getRefreshToken(),
                    (int) ((JwtTokenUtil.refreshTokenExpireMs/1000)));


            //메인페이지 이동
            return "redirect:/";
        }
        catch (CustomException e){
            //todo : message 띄워야하는데 나중에 추가구현해야함 1
            return "users/login";
        }
    }

    @GetMapping("/register") // 회원가입 페이지로 이동
    public String register(Model model){
        //dto object 전달
        model.addAttribute("userRegisterRequest",new UserRegisterRequestDto());
        return "users/register";
    }

    @PostMapping("/register") // 회원가입 요청
    public String registerRequest(
            @ModelAttribute UserRegisterRequestDto requestDto,
            Model model){
        try{
            userService.createUser(requestDto);
            return "redirect:/login";
        }
        catch (CustomException e){
            //todo : message 띄워야하는데 나중에 추가구현해야함 2
            return "users/register";
        }

    }

    @GetMapping("/my")
    public String myPage(Authentication authentication){
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        log.info("{} 님 마이페이지 입장",userEntity.getUserName());
        return "mypage";
    }


}
