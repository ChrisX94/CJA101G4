package com.shakemate.news.dto;

import java.sql.Timestamp;
import com.shakemate.news.model.News;

public class NewsDto {
	private Integer newsId;
	private Integer categoryId;
	private String title;
	private String content;

	// getters and setters
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getNewsId() {
		return newsId;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}
	
}
