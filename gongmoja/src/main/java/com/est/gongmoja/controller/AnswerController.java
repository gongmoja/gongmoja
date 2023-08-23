package com.est.gongmoja.controller;

import com.est.gongmoja.dto.answer.AnswerFormDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.service.AnswerService;
import com.est.gongmoja.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
//
//    // 답변저장
//    @PostMapping("/create/{id}")
//    public String createAnswer(Model model, @PathVariable("id") Long id, @Valid AnswerFormDto answerForm, BindingResult bindingResult){
//        QuestionEntity questionEntity = this.questionService.getQuestion(id);
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("question", questionEntity);
//            return "question_detail";
//        }
//
//        this.answerService.create(questionEntity, answerForm.getContent());
//        return String.format("redirect:/question/detail/%s", id);
//    }
}
