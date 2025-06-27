package com.shakemate.newstype.controller;

import com.shakemate.newstype.model.NewsType;
import com.shakemate.newstype.model.NewsTypeRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/newstype")
public class NewsTypeController {

    @Autowired
    private NewsTypeRepository newsTypeRepository;

    @GetMapping
    public List<NewsType> getAll() {
    	System.out.println("/api/newstype 被呼叫！");
        return newsTypeRepository.findAll();
    }
    
    @PostConstruct
    public void init() {
        System.out.println("NewsTypeController 已被 Spring 掃描並初始化");
    }
    
}
