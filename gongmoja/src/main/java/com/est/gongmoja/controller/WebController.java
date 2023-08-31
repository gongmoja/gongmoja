package com.est.gongmoja.controller;

import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.jwt.CookieUtil;
import com.est.gongmoja.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {
    private final UserService userService;


    @GetMapping("/")
    public String mainPage(Model model, Authentication authentication){
        //비 로그인이면 바로 메인페이지 리턴
        if(authentication == null){
            return "index";
        }

        //만일 로그인 했으면 user 객체를 가져온다
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());

        //model 에 userEntity 객체 넣는다
        model.addAttribute("userEntity",userEntity);

        return "index";
    }
}
