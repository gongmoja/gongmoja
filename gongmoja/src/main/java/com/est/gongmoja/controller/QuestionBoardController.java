package com.est.gongmoja.controller;

import com.est.gongmoja.dto.question.QuestionDto;
import com.est.gongmoja.service.QuestionBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionBoardController {

    @Autowired
    private QuestionBoardService questionBoardService;

    @GetMapping
    public String getAllQuestions(Model model) {
//        List<QuestionDto> questionList = questionBoardService.getAllQuestions();
//        model.addAttribute("questionList", questionList);
        return "questions_list";
    }

    
}
