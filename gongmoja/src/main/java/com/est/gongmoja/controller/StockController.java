package com.est.gongmoja.controller;

import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;

    @GetMapping("/{stockId}")
    public String displayStock(Model model, @PathVariable("stockId")Long stockId){
        StockEntity stock = stockService.findStockById(stockId);
        model.addAttribute("stock", stock);
        return "stock";
    }

}
