package com.shakemate.news.controller;

import com.shakemate.news.model.News;
import com.shakemate.news.model.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    // 建立一筆 News
    @PostMapping
    public ResponseEntity<News> create(@RequestBody News news) {
        News created = newsService.save(news);
        return ResponseEntity.ok(created);
    }

    // 取得所有 News
    @GetMapping
    public ResponseEntity<List<News>> getAll() {
        List<News> list = newsService.getAll();
        return ResponseEntity.ok(list);
    }

    // 根據 ID 取得單一 News
    @GetMapping("/{id}")
    public ResponseEntity<News> getById(@PathVariable Integer id) {
        News news = newsService.findById(id);
        if (news == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(news);
    }

    // 更新一筆 News
    @PutMapping("/{id}")
    public ResponseEntity<News> update(
            @PathVariable Integer id,
            @RequestBody News news) {

        News existing = newsService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        news.setNewsId(id);
        News updated = newsService.update(news);
        return ResponseEntity.ok(updated);
    }

    // 刪除一筆 News
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        News existing = newsService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
