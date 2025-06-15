package com.shakemate.shshop.dto;

import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdPic;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
public class ShProdDto {
    private Integer prodId;
    private Integer user;
    private String userName;
    private String prodName;
    private Integer prodPrice;
    private String prodBrand;
    private Integer prodCount;
    private String prodTypeName;
    private Integer prodTypeId;
    private String prodDesc;
    private String prodContent;
    private byte prodStatus;
    private String  prodStatusStr;
    private List<String> picUrls;
    private Timestamp updatedTime;
    private Integer prodViews;



    public ShProdDto(ShProd p) {
        this.prodId = p.getProdId();
        this.user = p.getUser().getUserId();
        this.userName = p.getUser().getUsername();
        this.prodName = p.getProdName();
        this.prodPrice = p.getProdPrice();
        this.prodBrand = p.getProdBrand();
        this.prodCount = p.getProdCount();
        this.prodDesc = p.getProdStatusDesc();
        this.prodContent = p.getProdContent();
        this.prodStatus = p.getProdStatus();
        this.prodStatusStr = getProdStatusStr(prodStatus);
        this.prodTypeName = p.getShProdType().getProdTypeName(); // 假設類別名稱欄位
        this.picUrls = new ArrayList<>();
        for (ShProdPic pic : p.getProdPics()) {
            this.picUrls.add(pic.getProdPic());
        }
        this.updatedTime = p.getUpdatedTime();
        this.prodViews = p.getProdViews();
    }

    public ShProdDto simpleInfo(ShProd p){
        ShProdDto shProdDto = new ShProdDto();
        this.prodId = p.getProdId();
        this.prodName = p.getProdName();
        return shProdDto;
    }
    public ShProdDto forUpdateDisplay(ShProd p) {
        this.prodId = p.getProdId();
        this.prodName = p.getProdName();
        this.prodPrice = p.getProdPrice();
        this.prodBrand = p.getProdBrand();
        this.prodCount = p.getProdCount();
        this.prodDesc = p.getProdStatusDesc();
        this.prodContent = p.getProdContent();
        this.prodStatus = p.getProdStatus();
        this.prodStatusStr = getProdStatusStr(prodStatus);
        this.prodTypeId = p.getShProdType().getProdTypeId();
        this.picUrls = new ArrayList<>();
        for (ShProdPic pic : p.getProdPics()) {
            this.picUrls.add(pic.getProdPic());
        }
        return this;
    }


    public  String getProdStatusStr(byte prodStatus) {
        String result = "";
        switch (prodStatus) {
            case 0: result = "審核中";
            break;
            case 1: result = "審核不通過";
            break;
            case 2: result = "已上架";
            break;
            case 3: result = "已下架";
            default:
                result = "審核中";
                break;
        }
        return result;
    }


    @Override
    public String toString() {
        return "ShProdDto{" +
                "prodId=" + prodId +
                ", user=" + user +
                ", userName='" + userName + '\'' +
                ", prodName='" + prodName + '\'' +
                ", prodPrice=" + prodPrice +
                ", prodBrand='" + prodBrand + '\'' +
                ", prodCount=" + prodCount +
                ", prodTypeName='" + prodTypeName + '\'' +
                ", prodTypeId=" + prodTypeId +
                ", prodDesc='" + prodDesc + '\'' +
                ", prodContent='" + prodContent + '\'' +
                ", prodStatus=" + prodStatus +
                ", prodStatusStr='" + prodStatusStr + '\'' +
                '}';
    }
}