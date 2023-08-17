package com.est.gongmoja.controller;

import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @GetMapping("/") // 메인페이지 ( 추후 컨트롤러 다른 곳으로 이동해야함 임시로 )
    public String mainPage(){
        return "main";
    }

    @GetMapping("/login") // 로그인 페이지로 이동
    public String login(){
        return "users/login";
    }

    @PostMapping("/login") // 로그인 요청
    public String loginRequest(){
        return "main";
    }

    @GetMapping("/register") // 회원가입 페이지로 이동
    public String register(){
        return "users/register";
    }

    @PostMapping("/register") // 회원가입 요청
    public String registerRequest(){
        return "main";
    }


}
