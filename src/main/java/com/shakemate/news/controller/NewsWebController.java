package com.shakemate.news.controller;

import com.shakemate.news.dto.NewsDto;
import com.shakemate.news.dto.NewsResponse;
import com.shakemate.news.service.NewsService;
import com.shakemate.newstype.model.NewsType;
import com.shakemate.newstype.repository.NewsTypeRepository;
import com.shakemate.adm.model.AdmRepository;
import com.shakemate.adm.model.AdmVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsWebController {

    @Autowired private NewsService newsService;
    @Autowired private NewsTypeRepository newsTypeRepo;

    @Autowired
    private AdmRepository admRepository;

    
    @ModelAttribute("allTypes")
    public List<NewsType> populateNewsTypes() {
        return newsTypeRepo.findAll();
    }

    @GetMapping
    public String list(Model model) {
        List<NewsResponse> list = newsService.getLatestNews();
        model.addAttribute("newsList", list);
        return "back-end/news/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("newsDto", new NewsDto());
        return "back-end/news/add";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("newsDto") NewsDto dto) {
        newsService.saveOrUpdate(dto);
        return "redirect:/news";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        NewsDto dto = newsService.getNewsDtoById(id);
        model.addAttribute("newsDto", dto);
        return "back-end/news/edit";
    }
    
    // 這個方法處理更新現有的新聞
    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("newsDto") NewsDto dto) {
        dto.setNewsId(id); // 確保 DTO 具有正確的 ID，用於更新
        newsService.saveOrUpdate(dto); // 假設 saveOrUpdate 方法能根據 ID 處理更新
        return "redirect:/news"; // 更新後重定向到新聞列表
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        newsService.deleteById(id);
        return "redirect:/admin/news";
    }
}
