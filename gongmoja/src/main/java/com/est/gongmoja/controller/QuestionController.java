package com.est.gongmoja.controller;

import com.est.gongmoja.dto.answer.AnswerFormDto;
import com.est.gongmoja.dto.question.QuestionFormDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.repository.QuestionRepository;
import com.est.gongmoja.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/question")
@Controller
@Slf4j
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    // 질문 등록
    @GetMapping("/create")
    public String questionCreate(@Valid QuestionFormDto questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        } this.questionService.create(questionForm.getTitle(), questionForm.getContent());
        return "redirect:/question/list";
    }

    // 질문 저장
    @PostMapping("/create")
    public String questionCreate(@RequestParam String title, @RequestParam String content) {
        this.questionService.create(title, content);
        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }

    // 질문 목록 조회
    @GetMapping("/list")
    public String list(Model model) {
        List<QuestionEntity> questionList = this.questionService.getList();
        model.addAttribute("questionList", questionList);
        return "question_list";
    }

    // 단일 질문 조회
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerFormDto answerForm) {
        QuestionEntity questionEntity = this.questionService.getQuestion(id);
        model.addAttribute("question", questionEntity);
        return "question_detail";
    }
}