package com.shakemate.news.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;                     
import org.springframework.web.bind.annotation.GetMapping;

import com.shakemate.news.service.NewsService;
import com.shakemate.newstype.repository.NewsTypeRepository;

@Controller
public class NhomeController {

    @Autowired
    private NewsTypeRepository typeRepo;

    @Autowired
    private NewsService newsService;

    @GetMapping("/nindex")
    public String newsPage(Model model) {
        model.addAttribute("allTypes", typeRepo.findAll());
        model.addAttribute("newsList", newsService.getLatestNews());
        return "front-end/news/nindex";
    }
}
