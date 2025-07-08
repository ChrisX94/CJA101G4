package com.shakemate.news.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.shakemate.news.repository.NewsRepository;
import com.shakemate.newstype.repository.NewsTypeRepository;
import com.shakemate.news.dto.NewsDto;
import com.shakemate.news.dto.NewsResponse;
import com.shakemate.news.model.News;
import com.shakemate.newstype.model.NewsType;
import com.shakemate.adm.model.AdmVO;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NewsService {
    @Autowired private NewsRepository newsRepo;
    @Autowired private NewsTypeRepository typeRepo;
    
 // 依類別取得新聞列表
    public List<NewsResponse> getNewsByCategory(Integer categoryId) {
        return newsRepo.findByNewsType_CategoryId(categoryId).stream()
                       .map(NewsResponse::new)
                       .collect(Collectors.toList());
    }

    // 建立或更新新聞
    public NewsResponse saveOrUpdate(NewsDto dto) {
        NewsType type = typeRepo.findById(dto.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("類別不存在"));
        News news = (dto.getNewsId() != null)
            ? newsRepo.findById(dto.getNewsId())
                .orElseThrow(() -> new EntityNotFoundException("新聞不存在"))
            : new News();
        news.setNewsType(type);
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setAdminId(dto.getAdminId());
        news.setNewsStatus(true);
        news = newsRepo.save(news);
        return new NewsResponse(news);
    }

    // 取得最新新聞列表
    public List<NewsResponse> getLatestNews() {
        return newsRepo.findTop10ByOrderByPublishTimeDesc().stream()
            .map(NewsResponse::new)
            .collect(Collectors.toList());
    }

    // 依 ID 取得單筆 NewsDto
    public NewsDto getNewsDtoById(Integer id) {
        News news = newsRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("新聞不存在"));
        NewsDto dto = new NewsDto();
        dto.setNewsId(news.getNewsId());
        dto.setCategoryId(news.getNewsType().getCategoryId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        return dto;
    }

    // 刪除新聞
    public void deleteById(Integer id) {
        if (!newsRepo.existsById(id)) {
            throw new EntityNotFoundException("新聞不存在");
        }
        newsRepo.deleteById(id);
    }
}