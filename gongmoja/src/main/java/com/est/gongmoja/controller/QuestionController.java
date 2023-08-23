package com.est.gongmoja.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;



@RequiredArgsConstructor
@RestController
@Slf4j
public class QuestionController {
    @GetMapping("/question/write") //localhost:8080/question/write
    public String questionWriteForm(){
        return "questionwrite";
    }


}