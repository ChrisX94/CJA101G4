package com.shakemate.shshop.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "SH_PROD_PIC")
public class ShProdPic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROD_PIC_ID")
    private Integer prodPicId;


    @Column(name = "PROD_PIC", length = 300)
    private String prodPic;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "PROD_ID", nullable = false)
    private ShProd shProd;

    public ShProdPic() {
        super();
    }


    public ShProdPic(Integer prodPicId, String prodPic, ShProd shProd) {
        this.prodPicId = prodPicId;
        this.prodPic = prodPic;
        this.shProd = shProd;
    }

    public ShProdPic(Integer prodPicId, String prodPic) {
        this.prodPicId = prodPicId;
        this.prodPic = prodPic;
    }

    public Integer getProdPicId() {
        return prodPicId;
    }

    public void setProdPicId(Integer prodPicId) {
        this.prodPicId = prodPicId;
    }

    public String getProdPic() {
        return prodPic;
    }

    public void setProdPic(String prodPic) {
        this.prodPic = prodPic;
    }

    public ShProd getShProd() {
        return shProd;
    }

    public void setShProd(ShProd shProd) {
        this.shProd = shProd;
    }

    @Override
    public String toString() {
        return "ShProdPic{" +
                "prodPicId=" + prodPicId +'}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShProdPic shProdPic)) return false;
        return Objects.equals(prodPicId, shProdPic.prodPicId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prodPicId);
    }
}