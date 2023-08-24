package com.est.gongmoja.controller;

import com.est.gongmoja.dto.answer.AnswerFormDto;
import com.est.gongmoja.dto.question.QuestionFormDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.service.QuestionService;
import com.est.gongmoja.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    // 질문 전체 목록 리스트 화면 http://localhost:8080/question/list
    // TODO: admin만 보이게 권한 수정해야함
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        List<QuestionEntity> questionEntity = questionService.getList();
        log.info("page:{}, kw:{}", page, kw);
        model.addAttribute("questionEntity", questionEntity);

        return "question/question_list";
    }

    // 질문 단일 목록 리스트 화면 http://localhost:8080/question/detail/{id}
    // TODO: user가 질문 목록 누르면 보이게 수정해야함
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerFormDto answerFormDto) {
        QuestionEntity question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question/question_detail";
    }

    // 질문 등록 화면 http://localhost:8080/question/create
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model) {
        model.addAttribute("questionFormDto", new QuestionFormDto());
        return "question/question_form";
    }

    // 질문 등록 처리 http://localhost:8080/question/create
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(String Subject, String content, @Valid QuestionFormDto questionFormDto, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return "question/question_form";
        }

        this.questionService.create(questionFormDto.getSubject(), questionFormDto.getContent());
        return "redirect:/question/list";
    }
}