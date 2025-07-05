package com.shakemate.casetype.model;

import jakarta.persistence.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "casetype")
public class CaseType implements Serializable {

	// 版本控制 ID，確保序列化／反序列化的相容性
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CASE_TYPE_ID")
    private Integer caseTypeId;

    @Column(name = "TYPE_NAME")
    private String typeName;
    
    @Column(name = "TYPE_DESC")
    private String typeDesc;
    
    // 無參構造器
    public CaseType() {
    }

    // getter / setter
    public Integer getCaseTypeId() { return caseTypeId; }
    public void setCaseTypeId(Integer caseTypeId) { this.caseTypeId = caseTypeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getTypeDesc() { return typeDesc; }
    public void setTypeDesc(String typeDesc) { this.typeDesc = typeDesc; }
}
