package com.shakemate.shshop.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "SH_PROD")
public class ShProd implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROD_ID")
    private Integer prodId;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "USER_ID", nullable = false)
    private Users user;

    @NotEmpty(message="商品名稱: 請勿空白")
    @Pattern(
            regexp = "^[\\p{IsHan}\\p{L}\\p{N}\\p{Zs}\\p{Punct}，。！？【】《》“”‘’：；、（）—．·]{1,50}$",
            message = "商品內容只能包含中、英文、日韓法文字、數字與標點，最多50字"
    )
    @Column(name = "PROD_NAME")
    private String prodName;

    @ManyToOne
    @JoinColumn(name = "PROD_TYPE_ID", nullable = false)
    private ShProdType shProdType;

    @Column(name = "PROD_CONTENT")
    @NotEmpty(message="商品內容: 請勿空白")
    private String prodContent;

    @NotEmpty(message="商品敘述: 請勿空白")
    @Column(name = "PROD_STATUS_DESC")
    private String prodStatusDesc;

    @NotNull(message = "商品價格: 請勿空白")
    @Min(value = 1, message = "商品價格: 不能小於 {value}")
    @Max(value = 30000, message = "商品價格: 不能超過 {value}")
    @Column(name = "PROD_PRICE")
    private Integer prodPrice;

    @NotEmpty(message="商品品牌: 請勿空白")
    @Pattern(
            regexp = "^[\\p{IsHan}\\p{L}\\p{N}\\p{Zs}\\p{Punct}，。！？【】《》“”‘’：；、（）—．·]{1,50}$",
            message = "商品內容只能包含中、英文、日韓法文字、數字與標點，最多50字"
    )
    @Column(name = "PROD_BRAND")
    private String prodBrand;

    @NotNull(message = "商品數量: 請勿空白")
    @Min(value = 0, message = "商品數量: 不能小於 {value}")
    @Max(value = 10, message = "商品數量: 不能超過 {value}")
    @Column(name = "PROD_COUNT")
    private Integer prodCount;

    @Column(name = "PROD_VIEWS")
    private Integer prodViews;

    @Column(name = "PROD_STATUS")
    private byte prodStatus;

    @Column(name = "PROD_REG_TIME", updatable = false)
    @CreationTimestamp
    private Timestamp prodRegTime;

    @Column(name = "UPDATED_TIME")
    @UpdateTimestamp
    private Timestamp updatedTime;


    @OneToMany(mappedBy = "shProd", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ShProdPic> prodPics;


    @OneToMany(mappedBy = "shProd", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ShOrder> orders;

    public ShProd() {
        super();
    }

    public ShProd(Integer prodId, Users user, String prodName, ShProdType shProdType, String prodContent, String prodStatusDesc, Integer prodPrice, String prodBrand, Integer prodCount, Integer prodViews, byte prodStatus, Timestamp prodRegTime, Timestamp updatedTime) {
        this.prodId = prodId;
        this.user = user;
        this.prodName = prodName;
        this.shProdType = shProdType;
        this.prodContent = prodContent;
        this.prodStatusDesc = prodStatusDesc;
        this.prodPrice = prodPrice;
        this.prodBrand = prodBrand;
        this.prodCount = prodCount;
        this.prodViews = prodViews;
        this.prodStatus = prodStatus;
        this.prodRegTime = prodRegTime;
        this.updatedTime = updatedTime;
    }

    public ShProd(Integer prodId, Users user, String prodName, ShProdType shProdType, String prodContent, String prodStatusDesc, Integer prodPrice, String prodBrand, Integer prodCount) {
        this.prodId = prodId;
        this.user = user;
        this.prodName = prodName;
        this.shProdType = shProdType;
        this.prodContent = prodContent;
        this.prodStatusDesc = prodStatusDesc;
        this.prodPrice = prodPrice;
        this.prodBrand = prodBrand;
        this.prodCount = prodCount;
        this.prodViews = 0;
        this.prodStatus = 0;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.prodRegTime = now;
        this.updatedTime = now;
    }

    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public ShProdType getShProdType() {
        return shProdType;
    }

    public void setProdType(ShProdType shProdType) {
        this.shProdType = shProdType;
    }

    public String getProdContent() {
        return prodContent;
    }

    public void setProdContent(String prodContent) {
        this.prodContent = prodContent;
    }

    public String getProdStatusDesc() {
        return prodStatusDesc;
    }

    public void setProdStatusDesc(String prodStatusDesc) {
        this.prodStatusDesc = prodStatusDesc;
    }

    public Integer getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(Integer prodPrice) {
        this.prodPrice = prodPrice;
    }

    public String getProdBrand() {
        return prodBrand;
    }

    public void setProdBrand(String prodBrand) {
        this.prodBrand = prodBrand;
    }

    public Integer getProdCount() {
        return prodCount;
    }

    public void setProdCount(Integer prodCount) {
        this.prodCount = prodCount;
    }

    public Integer getProdViews() {
        return prodViews;
    }

    public void setProdViews(Integer prodViews) {
        this.prodViews = prodViews;
    }

    public byte getProdStatus() {
        return prodStatus;
    }

    public void setProdStatus(byte prodStatus) {
        this.prodStatus = prodStatus;
    }

    public Timestamp getProdRegTime() {
        return prodRegTime;
    }

    public void setProdRegTime(Timestamp prodRegTime) {
        this.prodRegTime = prodRegTime;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    public List<ShProdPic> getProdPics() {
        return prodPics;
    }
    public void setProdPics(List<ShProdPic> prodPics) {
        this.prodPics = prodPics;
    }

    public void setShProdType(ShProdType shProdType) {
        this.shProdType = shProdType;
    }

    public List<ShOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ShOrder> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "ShProd{" +
                "prodId=" + prodId +
                ", user=" + user +
                ", prodName='" + prodName + '\'' +
                ", shProdType=" + shProdType +
                ", prodContent='" + prodContent + '\'' +
                ", prodStatusDesc='" + prodStatusDesc + '\'' +
                ", prodPrice=" + prodPrice +
                ", prodBrand='" + prodBrand + '\'' +
                ", prodCount=" + prodCount +
                ", prodViews=" + prodViews +
                ", prodStatus=" + prodStatus +
                ", prodRegTime=" + prodRegTime +
                ", updatedTime=" + updatedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShProd shProd)) return false;
        return Objects.equals(prodId, shProd.prodId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prodId);
    }
}
