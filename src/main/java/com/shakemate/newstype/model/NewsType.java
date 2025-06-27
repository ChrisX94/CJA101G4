package com.shakemate.newstype.model;

import jakarta.persistence.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import com.shakemate.news.model.News;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "newstype")
public class NewsType implements Serializable {


    @Id
    @Column(name = "CATEGORY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(name = "CATEGORY_NAME", length = 50, nullable = false)
    private String categoryName;
    
    @Column(name = "NEWS_DESC", length = 100, nullable = false)
    private String newsDesc;

    
    // getter / setter
	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getNewsDesc() {
		return newsDesc;
	}

	public void setNewsDesc(String newsDesc) {
		this.newsDesc = newsDesc;
	}
	
	@OneToMany(mappedBy = "newsType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore // 防止雙向關聯導致 JSON 無限循環
	private List<News> newsList;

}
