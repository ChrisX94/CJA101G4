package com.shakemate.shshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProdAuditResult {
    private Integer prodId;
    private String prodName;
    private String status;
    private String reason;

    public ProdAuditResult(Integer prodId, String prodName, String status, String reason) {
        this.prodId = prodId;
        this.prodName = prodName;
        this.status = status;
        this.reason = reason;
    }

    public ProdAuditResult(Integer prodId, String prodName, String status) {
        this.prodId = prodId;
        this.prodName = prodName;
        this.status = status;
    }


}
