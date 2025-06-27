package com.shakemate.news.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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

	@Column(name = "PUBLISH_TIME", updatable = false)
	private Timestamp publishTime;

	@PrePersist
	protected void onPublish() {
		this.publishTime = new Timestamp(System.currentTimeMillis());
	}

	// 若未來有 News 與 Admin 關聯可改為 ManyToOne
	@Column(name = "ADM_ID")
	private Integer admId;

	@Column(name = "NEWS_STATUS")
	private boolean newsStatus = false;

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

	public Integer getAdmId() {
		return admId;
	}

	public void setAdmId(Integer admId) {
		this.admId = admId;
	}

	public boolean isNewsStatus() {
		return newsStatus;
	}

	public void setNewsStatus(boolean newsStatus) {
		this.newsStatus = newsStatus;
	}
}

// 只讀的 FK 欄位(建議移除，因為會從 NewsType 抓)
// @Column(name = "CATEGORY_ID", insertable = false, updatable = false)
// private Integer categoryId;

// (會從 NewsType 抓 CATEGORY_ID)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "CATEGORY_ID", nullable = false)
//	private NewsType newsType;

//	ADM FK 關聯
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ADM_ID", nullable = false)
//	private AdmVO admin;
//
//	ADM FK 關聯 處理好後需要刪除	
//	@Column(name = "ADM_ID")
//	private Integer admId;