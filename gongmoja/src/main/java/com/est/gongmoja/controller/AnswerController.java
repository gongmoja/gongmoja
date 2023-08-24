package com.est.gongmoja.controller;

import com.est.gongmoja.dto.answer.AnswerFormDto;
import com.est.gongmoja.entity.AnswerEntity;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.service.AnswerService;
import com.est.gongmoja.service.QuestionService;
import com.est.gongmoja.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/answer")
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id,
                               @Valid AnswerFormDto answerFormDto, BindingResult bindingResult) {
        QuestionEntity question = this.questionService.getQuestion(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question/question_detail";
        }
        AnswerEntity answer = this.answerService.create(question,
                answerFormDto.getContent());
//        return String.format("redirect:/question/detail/%s#answer_%s", answer.getId(), answer.getId());
        return "redirect:/question/detail/" + id + "#answer" + answer.getId();
    }
    }