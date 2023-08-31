package com.est.gongmoja.controller;

import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.service.StockService;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final UserService userService;
    private final StockService stockService;

    @PostMapping("/add/{stockId}")
    @ResponseBody
    public String addFavoriteStock(@PathVariable Long stockId, Authentication authentication) {
        UserEntity user = getCurrentUser(authentication);

        StockEntity stock = stockService.getStockById(stockId);
        if (user != null && stock != null) {
            user.getStocks().add(stock);
            userService.saveUser(user);
            return "주식이 즐겨찾기 되었습니다.";
        } else {
            return "user 또는 주식을 찾지 못하였습니다.";
        }
    }

    @PostMapping("/remove/{stockId}")
    @ResponseBody
    public ResponseEntity<String> removeFavoriteStock(@PathVariable Long stockId, Authentication authentication) {
        UserEntity user = getCurrentUser(authentication);

        StockEntity stock = stockService.getStockById(stockId);
        if (user != null && stock != null) {
            user.getStocks().remove(stock);
            userService.saveUser(user);
            return ResponseEntity.ok("즐겨찾기에서 제거되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("user 또는 주식을 찾지 못하였습니다.");
        }
    }

    @GetMapping("/list")
    public String getFavoriteStocks(Model model, Authentication authentication) {
        UserEntity user = getCurrentUser(authentication);

        if (user != null) {
            model.addAttribute("username", user.getUserName());
            model.addAttribute("favoriteStocks", user.getStocks());
        } else {
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        }
//        return "stock/favorite-stocks";
        return "/mypage";
    }


    @GetMapping("/isFavorite/{stockId}")
    @ResponseBody
    public boolean isFavoriteStock(@PathVariable Long stockId, Authentication authentication) {
        UserEntity user = getCurrentUser(authentication);

        if (user != null) {
            StockEntity stock = stockService.getStockById(stockId);
            return user.getStocks().contains(stock);
        }
        return false;
    }

    private UserEntity getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserEntity userEntity = (UserEntity) authentication.getPrincipal();
            return userService.getUser(userEntity.getUserName());
        }
        return null;
    }
}
