package com.example.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//회원가입 후 메인 페이지로 갈 수 있도록하는 소스

@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "main";
    }
}
