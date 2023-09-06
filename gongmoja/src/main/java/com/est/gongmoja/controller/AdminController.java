package com.est.gongmoja.controller;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.service.QuestionService;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final QuestionService questionService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')") // 전체 리스트 admin만 접근 가능하도록 설정
    @GetMapping("/admin")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity user1 = userService.getUser(user.getUserName());
        model.addAttribute("userEntity", user1);

        Page<QuestionEntity> paging = questionService.getList(page);
        model.addAttribute("paging", paging);

        return "question/question_list_admin";
    }
}
