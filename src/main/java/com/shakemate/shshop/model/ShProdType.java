package com.shakemate.shshop.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "SH_PROD_TYPE")
public class ShProdType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROD_TYPE_ID")
    private Integer prodTypeId;

    @Column(name = "PROD_TYPE_NAME", length = 50, nullable = false)
    private String prodTypeName;

    @OneToMany(mappedBy = "shProdType")
    private List<ShProd> shProds = new ArrayList<>();


    public ShProdType() {
        super();
    }

    public ShProdType(Integer prodTypeId, String prodTypeName, List<ShProd> shProds) {
        this.prodTypeId = prodTypeId;
        this.prodTypeName = prodTypeName;
        this.shProds = shProds;
    }

    public ShProdType(String prodTypeName) {
        this.prodTypeName = prodTypeName;
    }

    public ShProdType(Integer prodTypeId, String prodTypeName) {
    }

    // Getter & Setter

    public Integer getProdTypeId() {
        return prodTypeId;
    }

    public void setProdTypeId(Integer prodTypeId) {
        this.prodTypeId = prodTypeId;
    }

    public String getProdTypeName() {
        return prodTypeName;
    }

    public void setProdTypeName(String prodTypeName) {
        this.prodTypeName = prodTypeName;
    }

    public List<ShProd> getShProds() {
        return shProds;
    }


    public void setShProds(List<ShProd> shProds) {
        this.shProds = shProds;
    }

    @Override
    public String toString() {
        return "ShProdType{" +
                "prodTypeId=" + prodTypeId +
                ", prodTypeName='" + prodTypeName +'}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShProdType that)) return false;
        return Objects.equals(prodTypeId, that.prodTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prodTypeId);
    }
}
