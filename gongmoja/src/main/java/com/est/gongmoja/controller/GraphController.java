package com.est.gongmoja.controller;

import com.est.gongmoja.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;

    @GetMapping("/display-graph")
    public String displayGraph(Model model) {
        try {
            String base64Image = graphService.generateGraph();
            model.addAttribute("base64Image", base64Image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "graph";
    }
}
