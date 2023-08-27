package com.est.gongmoja.controller;

import com.est.gongmoja.dto.answer.AnswerFormDto;
import com.est.gongmoja.dto.question.QuestionFormDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.service.QuestionService;
import com.est.gongmoja.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<QuestionEntity> paging = questionService.getList(page);
        model.addAttribute("paging", paging);
        return "question/question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerFormDto answerFormDto) {
        QuestionEntity question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model) {
        model.addAttribute("questionFormDto", new QuestionFormDto());
        return "question/question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionFormDto questionFormDto,
                                 @RequestParam("file") MultipartFile imageFile,
                                 BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            return "question/question_form";
        }

        questionService.create(questionFormDto.getSubject(), questionFormDto.getContent(), imageFile);
        return "redirect:/question/list";
    }
}


    // 질문 등록 처리 http://localhost:8080/question/create
//    @PreAuthorize("isAuthenticated()") // 로그인 안하면 질문 작성 안됨
//    @PostMapping("/create")
//    public String questionCreate(@Valid QuestionFormDto questionFormDto,
//                                  BindingResult bindingResult, Principal principal) throws IOException {
//
//        if (bindingResult.hasErrors()) {
//            return "question/question_form";
//        }
//
//        UserEntity userEntity = this.userService.getUser(principal.getName());
//        this.questionService.create(questionFormDto.getSubject(), questionFormDto.getContent(), userEntity);
//        return "redirect:/question/list";
//    }