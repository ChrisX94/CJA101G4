package com.shakemate.news.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.shakemate.news.service.NewsService;
import com.shakemate.news.dto.NewsDto;
import com.shakemate.news.dto.NewsResponse;
import com.shakemate.adm.model.AdmVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {
	@Autowired
	private NewsService newsSvc;

	@PostMapping
	public ResponseEntity<NewsResponse> postNews(@RequestBody NewsDto dto, HttpSession session) {
		AdmVO admin = (AdmVO) session.getAttribute("loggedInAdmin");
		if (admin == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		NewsResponse resp = newsSvc.saveOrUpdate(dto, admin);
		return ResponseEntity.ok(resp);
	}

	@GetMapping("/latest")
	public List<NewsResponse> latest() {
		return newsSvc.getLatestNews();
	}
	
	@GetMapping("/category/{categoryId}")
	public List<NewsResponse> byCategory(@PathVariable Integer categoryId) {
	    return newsSvc.getNewsByCategory(categoryId);
	}

}
