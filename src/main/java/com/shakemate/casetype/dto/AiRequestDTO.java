package com.shakemate.casetype.dto;

public class AiRequestDTO {
    /** 前端選的分類名稱 */
    private String selectedCategory;
    /** （可選）直接傳 raw user message */
    private String message;
    /** （可選）案件 ID，若做進度查詢 */
    private Integer caseId;
    
	public String getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(String selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getCaseId() {
		return caseId;
	}
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}

    
}
