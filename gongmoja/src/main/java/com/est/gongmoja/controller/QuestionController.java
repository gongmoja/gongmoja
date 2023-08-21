package com.est.gongmoja.controller;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.QuestionRepository;
import com.est.gongmoja.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    // 게시글 작성화면
   /* @GetMapping("/form")
    public String form(HttpSession session){
        // 로그인이 되어있지 않으면 로그인페이지로 이동하는 기능 추가하기
        if (!HttpSessionUtils.isLoginUser(session)){
            // 로그인이 되어있지않다면 로그인 페이지로 반환
            return "/users/loginForm"; // 추후 수정
        }
        return "question/form";
    }

    @PostMapping("")
    public String create(String title, String content, HttpSession session){
        // 로그인이 되어있지 않으면 로그인페이지로 이동하는 기능 추가하기
        if (!HttpSessionUtils.isLoginUser(session)){
            return "/users/loginForm"; // 추후 수정
        }
        UserEntity userEntity = HttpSessionUtils.getUserFromSession(session);

        QuestionEntity newQuestion = new QuestionEntity(userEntity.getId(), title, content);
        return "redirect:/";
    }*/
}