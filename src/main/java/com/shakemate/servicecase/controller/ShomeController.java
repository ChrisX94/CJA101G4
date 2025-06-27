package com.shakemate.servicecase.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShomeController {
	
	// 用來導向客服GPT頁面 sindex.html
    @GetMapping("/sindex")
    public String chatPage() {
        return "front-end/servicecase/sindex";
    }

}
