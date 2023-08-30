package com.est.gongmoja.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @RequestMapping("/gongmoja")
    public String main(){

        return "index";
    }
}
