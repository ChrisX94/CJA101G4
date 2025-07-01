// src/main/java/com/shakemate/casetype/dto/CaseTypeDTO.java
package com.shakemate.casetype.dto;

public class CaseTypeDTO {
    private Integer caseTypeId;
    private String typeName;
    private String typeDesc;

    public CaseTypeDTO() {}

    public Integer getCaseTypeId() { return caseTypeId; }
    public void setCaseTypeId(Integer caseTypeId) { this.caseTypeId = caseTypeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getTypeDesc() { return typeDesc; }
    public void setTypeDesc(String typeDesc) { this.typeDesc = typeDesc; }
}
