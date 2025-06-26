package com.shakemate.casetype.model;

import jakarta.persistence.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import java.util.List;
import com.shakemate.servicecase.model.ServiceCase;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "casetype")
public class CaseType implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer caseTypeId;

    private String typeName;
    private String typeDesc;

    // getter / setter
    public Integer getCaseTypeId() { return caseTypeId; }
    public void setCaseTypeId(Integer caseTypeId) { this.caseTypeId = caseTypeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getTypeDesc() { return typeDesc; }
    public void setTypeDesc(String typeDesc) { this.typeDesc = typeDesc; }
}
