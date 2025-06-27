package com.shakemate.news.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

    default News findByPrimaryKey(Integer newsId) {
        return findById(newsId).orElse(null);
    }

    // 因為 categoryId 實際上是存在 NewsType 中的 FK，
    // Spring Data JPA 推導需要寫成「findByNewsType_CategoryId」
    List<News> findByNewsType_CategoryId(Integer categoryId);

    List<News> findByNewsStatus(boolean newsStatus);

    List<News> findByAdmId(Integer admId); // 可選擇性開放
}
