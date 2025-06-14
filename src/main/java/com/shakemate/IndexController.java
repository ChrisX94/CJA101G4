package com.shakemate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @GetMapping("/")
    public String MyMethod() {
        return "index"; // --->  src\main\resources\templates\index1.html
    }
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // 對應 resources/templates/login.html
    }

    @GetMapping("/shprod")
    public String productPage() {
        return "forward:/shshop/select_page.html";
    }

}