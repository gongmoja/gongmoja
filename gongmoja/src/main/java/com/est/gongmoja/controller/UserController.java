package com.est.gongmoja.controller;

import com.est.gongmoja.dto.user.*;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.exception.ErrorHandler;
import com.est.gongmoja.jwt.CookieUtil;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.service.MailService;
import com.est.gongmoja.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final MailService mailService;

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
        if(!requestDto.getPassword().equals(requestDto.getPassword_check())){
            model.addAttribute("message", ErrorCode.NEW_PASSWORD_NOT_CORRECT.getMessage());
            model.addAttribute("searchUrl","/register");
            return "users/message";
        }
        try{
            userService.createUser(requestDto);
            return "redirect:/login";
        }
        catch (CustomException e){
            model.addAttribute("message",ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());
            model.addAttribute("searchUrl","/register");
            return "users/message";
        }

    }

//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/my")
//    public String myPage(Authentication authentication){
//        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
//        log.info("{} 님 마이페이지 입장",userEntity.getUserName());
//        return "mypage";
//    }

    @GetMapping("/forgot-password") //비밀번호 재발급 요청
    public String forgotPassword(Model model) {
        model.addAttribute("userForgotPasswordRequest",new UserForgotPasswordRequestDto());
        return "users/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordRequest(
            @ModelAttribute UserForgotPasswordRequestDto requestDto
    ){
        mailService.sendMail(requestDto.getEmail());
        return "redirect:/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify-password")//비밀번호 변경 요청
    public String modifyPassword(Model model, Authentication authentication){
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        model.addAttribute("userModifyPasswordRequest",new UserModifyPasswordRequestDto());
        model.addAttribute("userEntity", userEntity);
        return "users/modify-password";
    }

    @PostMapping("/modify-password")//TODO 소셜로그인 한 사람은 password 가 따로 없어서 관련해 로직 추가해야함
    public String modifyPasswordRequest(
            @ModelAttribute UserModifyPasswordRequestDto requestDto,
            Authentication authentication
    ){
        //유저객체 생성
        UserEntity temp = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(temp.getUserName());

        //새로운 비밀번호
        String newPassword = requestDto.getNewPassword();
        String newPasswordCheck = requestDto.getNewPasswordOneMoreTime();

        //기존 비밀번호
        String nowPassword = requestDto.getNowPassword();

        //현재 비밀번호가 맞는지 확인 (아니면 예외처리)
        if(!userService.checkPassword(nowPassword,userEntity)) throw new CustomException(ErrorCode.PASSWORD_ERROR);

        //새 비밀번호 & 새 비밀번호 확인 두개가 같은지 (아니면 예외처리)
        if(!newPassword.equals(newPasswordCheck)) throw new CustomException(ErrorCode.NEW_PASSWORD_NOT_CORRECT);

        //비밀번호 변경
        userService.modifyPassword(newPassword,userEntity);

        return "redirect:/";
    }

}
