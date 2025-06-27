package com.shakemate.news.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsService {

    @Autowired
    private NewsRepository repository;

    public News save(News news) {
        return repository.save(news);
    }

    public News update(News news) {
        return repository.save(news);
    }

    public void delete(Integer newsId) {
        if (repository.existsById(newsId)) {
            repository.deleteById(newsId);
        }
    }

    public News findById(Integer newsId) {
        return repository.findById(newsId).orElse(null);
    }

    public List<News> getAll() {
        return repository.findAll();
    }

    public List<News> findByCategoryId(Integer categoryId) {
        return repository.findByNewsType_CategoryId(categoryId);
    }

    public List<News> findByNewsStatus(boolean newsStatus) {
        return repository.findByNewsStatus(newsStatus);
    }

    public List<News> findByAdmId(Integer admId) {
        return repository.findByAdmId(admId);
    }
}
