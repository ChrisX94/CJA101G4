package com.shakemate.casetype.dto;

/**
 * 資料傳輸物件：代表前端點選的案件類別
 */
public class CaseTypeDTO {
    private Integer caseTypeId;
    private String typeName;
    private String typeDesc;
    
    /** 新增：前端選擇的問題類別或要傳給 AI 的使用者提示 */
    private String selectedCategory;

    public CaseTypeDTO() {}

    public Integer getCaseTypeId() {
        return caseTypeId;
    }

    public void setCaseTypeId(Integer caseTypeId) {
        this.caseTypeId = caseTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
}
