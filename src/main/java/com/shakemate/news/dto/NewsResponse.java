package com.shakemate.news.dto;

import java.sql.Timestamp;
import com.shakemate.news.model.News;

public class NewsResponse {
    private Integer newsId;
    private String title;
    private String content;
    private Timestamp publishTime;
    private String categoryName;
    private Integer adminId;  // 確保這裡有 adminId 屬性
//    private String adminName;
    
    

	public NewsResponse(News news) {
        this.newsId = news.getNewsId();
        this.title = news.getTitle();
        this.content = news.getContent();
        this.publishTime = news.getPublishTime();
        this.categoryName = news.getNewsType().getCategoryName();
//        this.adminId = news.getAdmin().getAdmName();
//        this.adminName = news.getAdmin().getAdmName();
    }

    // getters
    public Integer getNewsId() { return newsId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Timestamp getPublishTime() { return publishTime; }
    public String getCategoryName() { return categoryName; }
//    public String getAdminName() { return adminName; }

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}
}