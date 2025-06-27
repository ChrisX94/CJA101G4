package com.shakemate.news.controller;

import com.shakemate.newstype.model.*;
import com.shakemate.news.model.News;
import com.shakemate.news.model.NewsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/news")
public class NewsWebController {

	@Autowired
	private NewsService newsService;

	@Autowired
	private NewsTypeRepository newsTypeRepo;

	@ModelAttribute("allTypes")
	public List<NewsType> populateCaseTypes() {
		return newsTypeRepo.findAll();
	}

	// 顯示全部案件
	@GetMapping
	public String list(Model model) {
		model.addAttribute("news", newsService.getAll());
		return "back-end/news/list";
	}

	// 顯示新增表單
	@GetMapping("/add")
//    public String showAddForm(Model model) {
	public String showAddForm(Model model) {
		model.addAttribute("news", new News());
		return "back-end/news/add";
	}

	// 儲存新案件（或更新）
	@PostMapping("/save")
	public String save(@ModelAttribute News newsItem) {
		newsService.save(newsItem); // JPA save() 可用於新增或更新
		return "redirect:/news";
	}

	// 顯示修改頁面
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer newsId, Model model) {
		News editNews = newsService.findById(newsId);
		if (editNews == null) {
			return "redirect:/news?error=notfound";
		}
		model.addAttribute("news", editNews);
		return "back-end/news/edit";
	}

	// 刪除案件
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		newsService.delete(id);
		return "redirect:/news";
	}
}
