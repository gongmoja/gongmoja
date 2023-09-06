package com.est.gongmoja.controller;

import com.est.gongmoja.dto.question.QuestionFormDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.service.QuestionService;
import com.est.gongmoja.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    //    @PreAuthorize("hasRole('ROLE_ADMIN')") // 전체 리스트 admin만 접근 가능하도록 설정
    @GetMapping("/list")
    public String list( Principal principal, Model model, @RequestParam(value="page", defaultValue="0") int page, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity user1 = userService.getUser(user.getUserName());
        model.addAttribute("userEntity", user1);

        Page<QuestionEntity> paging = questionService.getList(page);
        model.addAttribute("paging", paging);

        log.info(principal.getName());
        return "question/question_list";
    }

    // 작성자 별 문의글 리스트
    @GetMapping("/list-by-user")
    public String listByUser(
            Authentication authentication,
            Model model,
            @RequestParam(value="page", defaultValue="0") int page)
    {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity user1 = userService.getUser(user.getUserName());
        model.addAttribute("userEntity", user1);

        Page<QuestionEntity> paging = questionService.getListByUser(page, user1.getId());
        model.addAttribute("paging", paging);
        return "question/question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity user1 = userService.getUser(user.getUserName());
        model.addAttribute("userEntity", user1);

        QuestionEntity question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/question_detail";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity user1 = userService.getUser(user.getUserName());
        model.addAttribute("userEntity", user1);
        model.addAttribute("questionFormDto", new QuestionFormDto());
        return "question/question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(Model model,
                                 @Valid QuestionFormDto questionFormDto,
                                 @RequestParam("file") MultipartFile imageFile, Authentication authentication) throws IOException {

        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());

        questionService.create(questionFormDto.getSubject(), questionFormDto.getContent(), imageFile, userEntity);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        log.info("질문 작성 완료");
        log.info("upload file name = {}", imageFile.getOriginalFilename());

        return "redirect:/question/list-by-user";
    }
}