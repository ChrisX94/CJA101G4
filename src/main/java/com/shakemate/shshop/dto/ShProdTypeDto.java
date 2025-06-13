package com.shakemate.shshop.dto;




import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Getter
@NoArgsConstructor
public class ShProdTypeDto {
    private Integer prodTypeId;
    private String prodTypeName;
    private List<ShProd> shProds;


    public ShProdTypeDto(Integer prodTypeId, String prodTypeName) {
        this.prodTypeId = prodTypeId;
        this.prodTypeName = prodTypeName;
    }
    public ShProdTypeDto(ShProdType shProdType) {
        this.prodTypeId = shProdType.getProdTypeId();
        this.prodTypeName = shProdType.getProdTypeName();
        this.shProds = new ArrayList<>();
        for (ShProd prod: shProdType.getShProds()){
            this.shProds.add(prod);
        }
    }


}
