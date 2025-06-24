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


    public String getTestData(){
        return """
                json
                [
                  {
                    "prodId": 12,
                    "prodName": "星空投影燈",
                    "status": "approve"
                  },
                  {
                    "prodId": 18,
                    "prodName": "姓名吊飾",
                    "status": "reject",
                    "reason": "圖片與商品無關，顯示的是攝影燈相關的圖片，違反規則第7條。"
                  },
                  {
                    "prodId": 19,
                    "prodName": "情侶任務本",
                    "status": "reject",
                    "reason": "圖片與商品無關，顯示的是三腳架，違反規則第7條。"
                  },
                  {
                    "prodId": 20,
                    "prodName": "商品87",
                    "status": "reject",
                    "reason": "商品名稱和描述模糊且不具體，違反規則第2條。"
                  }
                ]
                """;
    }

}
