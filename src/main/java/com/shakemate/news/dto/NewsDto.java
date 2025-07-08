package com.shakemate.news.dto;

import jakarta.validation.constraints.*;
//import java.sql.Timestamp;
//import com.shakemate.news.model.News;

public class NewsDto {
	private Integer newsId;
	
	@NotNull(message = "類別 ID 不可為空")
	private Integer categoryId;
	
    @NotBlank(message = "標題不可為空")
    @Size(max = 100, message = "標題最多 100 字")
	private String title;
    
    @NotBlank(message = "內容不可為空")
    @Size(max = 800, message = "內容最多 800 字")
	private String content;
	
	private Integer adminId; // 👈 加上這個

	@NotNull(message = "管理員 ID 不可為空")
	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

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
