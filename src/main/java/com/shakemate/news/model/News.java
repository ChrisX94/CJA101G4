package com.shakemate.news.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

import com.shakemate.adm.model.AdmVO;
import com.shakemate.newstype.model.NewsType;

@Entity
@Table(name = "news")
public class News implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NEWS_ID")
	private Integer newsId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID", nullable = false)
	private NewsType newsType;

	@Column(name = "TITLE", length = 100, nullable = false)
	private String title;

	@Column(name = "CONTENT", length = 800, nullable = false)
	private String content;

	@Column(name = "PUBLISH_TIME", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
	private Timestamp publishTime;

	@PrePersist
	protected void onPublish() {
        if (publishTime == null) {
            publishTime = new Timestamp(System.currentTimeMillis());
        }
	}

    // 管理員關聯：取代原本單純的 admId 欄位
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADM_ID", nullable = false)
    private AdmVO admin;

	@Column(name = "NEWS_STATUS", nullable = false)
	private boolean newsStatus;

	// Getters and Setters
	public Integer getNewsId() {
		return newsId;
	}

	public void setNewsId(Integer newsId) {
		this.newsId = newsId;
	}

	public NewsType getNewsType() {
		return newsType;
	}

	public void setNewsType(NewsType newsType) {
		this.newsType = newsType;
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

	public Timestamp getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}

	public boolean isNewsStatus() {
		return newsStatus;
	}

	public void setNewsStatus(boolean newsStatus) {
		this.newsStatus = newsStatus;
	}

	public AdmVO getAdmin() {
		return admin;
	}

	public void setAdmin(AdmVO admin) {
		this.admin = admin;
	}
}