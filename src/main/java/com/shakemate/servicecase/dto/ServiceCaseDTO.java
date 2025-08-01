// src/main/java/com/shakemate/servicecase/dto/ServiceCaseDTO.java
package com.shakemate.servicecase.dto;

import java.sql.Timestamp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import jakarta.validation.constraints.NotNull;


public class ServiceCaseDTO {
	private Integer caseId;
	
    /** 使用者、管理員 ID 都不能為空，空值交給 Spring 檢查 */
    @NotNull(message = "使用者 ID 不可為空")
	private Integer userId;
    
    @NotNull(message = "管理員 ID 不可為空")
	private Integer admId;
	
    private Integer caseTypeId;
	private String caseTypeName; // 額外欄位：直接顯示 CaseType 的名稱
	private Timestamp createTime;
	private Timestamp updateTime;
	
    @NotBlank(message = "標題不可為空")
    @Size(max = 100, message = "標題不能超過 100 字")
	private String title;
    
    @NotBlank(message = "內容不可為空")
    @Size(max = 800, message = "內容不能超過 800 字")
	private String content;
	private Integer caseStatus;
	
	
	/** 新增 email 欄位 */
    @NotBlank(message = "Email 不可為空")
    @Email(message = "Email 格式不正確")
	private String email;

	
	public ServiceCaseDTO() {
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

	public Integer getCaseTypeId() {
		return caseTypeId;
	}

	public void setCaseTypeId(Integer caseTypeId) {
		this.caseTypeId = caseTypeId;
	}

	public String getCaseTypeName() {
		return caseTypeName;
	}

	public void setCaseTypeName(String caseTypeName) {
		this.caseTypeName = caseTypeName;
	}

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

	// 加上這兩個方法：
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
