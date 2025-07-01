package com.shakemate.servicecase.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

import com.shakemate.casetype.model.CaseType;

@Entity
@Table(name = "servicecase")
public class ServiceCase implements Serializable {

	private static final long serialVersionUID = 1L;

	public ServiceCase() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CASE_ID")
	private Integer caseId;

	@Column(name = "USER_ID")
	private Integer userId;

	@Column(name = "ADM_ID")
	private Integer admId;

	// 只讀的 FK 欄位
	@Column(name = "CASE_TYPE_ID", insertable = false, updatable = false)
	private Integer caseTypeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CASE_TYPE_ID", nullable = false)
	//	@JsonIgnore 沒有雙向關聯的話不用加
	private CaseType caseType;

	@Column(name = "CREATE_TIME", updatable = false)
	private Timestamp createTime;

	@Column(name = "UPDATE_TIME")
	private Timestamp updateTime;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "CONTENT")
	private String content;

	@Column(name = "CASE_STATUS")
	private Integer caseStatus;

	@PrePersist
	protected void onCreate() {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		this.createTime = now;
		this.updateTime = now;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateTime = new Timestamp(System.currentTimeMillis());
	}

	public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getAdmId() {
		return admId;
	}

	public void setAdmId(Integer admId) {
		this.admId = admId;
	}

	// 只讀
	public Integer getCaseTypeId() {
		return this.caseTypeId;
	}

	// 避免意外改值，可以(刪除)不提供 setCaseTypeId(...)，或者把它設成 protected/private
	//	 private void setCaseTypeId(Integer caseTypeId) {
	//	 this.caseTypeId = caseTypeId;
	//	 }

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
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

	public Integer getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(Integer caseStatus) {
		this.caseStatus = caseStatus;
	}

	public CaseType getCaseType() {
		return caseType;
	}

	public void setCaseType(CaseType caseType) {
		this.caseType = caseType;
	}
	
	
	
    // getter / setter ...

    public String getStatusName() {
        return switch (caseStatus) {
            case 0 -> "未處理";
            case 1 -> "處理中";
            case 2 -> "已完成";
            default -> "未知";
        };
    }

}
