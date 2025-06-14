package com.shakemate.shshop.service;

import com.google.gson.Gson;
import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.shshop.dao.ShShopTypeRepository;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.dto.ShProdTypeDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdPic;
import com.shakemate.shshop.model.ShProdType;
import com.shakemate.user.model.Users;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service("ShShop")
public class ShShopService {

    @Autowired
    private ShShopRepository repository;

    @Autowired
    private ShShopTypeRepository tRepository;

    @Autowired
    private SessionFactory session;

    @Transactional(readOnly = true)
    public List<ShProdDto> getAll(){
        List<ShProd> list  = repository.findAllWithPics();
        List<ShProdDto> dtoList = new ArrayList<>();
        for(ShProd p : list){
            ShProdDto dto = new ShProdDto(p);
            dtoList.add(dto);
        }
        return dtoList ;
    }

    @Transactional(readOnly = true)
    public ShProdDto getById(int id){
        ShProd prod = repository.getById(id);
        ShProdDto dto =null;
        if(prod != null){
            dto = new ShProdDto(prod);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ShProdTypeDto> getAllType(){
        List<ShProdType> list  = tRepository.findAll();
        List<ShProdTypeDto> dtos = new ArrayList<>();
        for(ShProdType p : list){
            ShProdTypeDto dto = new ShProdTypeDto(p.getProdTypeId(), p.getProdTypeName());
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<ShProdDto> getProdsByType(Integer typeId){
        List<ShProd> list  = repository.getByType(typeId);
        List<ShProdDto> dtos = new ArrayList<>();
        for(ShProd p : list){
            ShProdDto dto = new ShProdDto(p);
            dtos.add(dto);
        }
        return dtos;

    }
    @Transactional(readOnly = true)
    public List<ShProdDto> getProdsByUser(Integer userId) {
        List<ShProd> list = repository.getByUserId(userId);
        List<ShProdDto> dtos = new ArrayList<>();
        for (ShProd p : list) {
            ShProdDto dto = new ShProdDto(p);
            dtos.add(dto);
        }
        return dtos;

    }

    @Transactional
    public ShProdDto createNewProduct(Integer userId, ShProd form, List<String> picUrls) {
        ShProd prod = new ShProd();
        Users user = new Users();
        user.setUserId(userId);
        prod.setUser(user);
        prod.setProdName(form.getProdName());
        prod.setProdBrand(form.getProdBrand());
        prod.setProdType(form.getShProdType());
        prod.setProdContent(form.getProdContent());
        prod.setProdStatusDesc(form.getProdStatusDesc());
        prod.setProdPrice(form.getProdPrice());
        prod.setProdCount(form.getProdCount());
        prod.setProdViews(0);
        prod.setProdStatus((byte) 0);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        prod.setProdRegTime(now);
        prod.setUpdatedTime(now);

        List<ShProdPic> picList = shProdPic(picUrls,prod);
        prod.setProdPics(picList);


        return new ShProdDto(repository.save(prod));
    }


    private List<ShProdPic> shProdPic(List<String> shProdPicUrl, ShProd shProd) {
        List<ShProdPic> pics = shProd.getProdPics();
        if (pics == null) {
            // 新增時：原 list 為 null，初始化
            pics = new ArrayList<>();
            shProd.setProdPics(pics);
        } else {
            // 更新時：清除舊圖（會觸發 Hibernate orphanRemoval 自動刪除）
            pics.clear();
        }
        // 新增新圖（無論是新增或更新）
        if (shProdPicUrl != null && shProdPicUrl.size() > 0) {
            for (String url : shProdPicUrl) {
                ShProdPic newPic = new ShProdPic();
                newPic.setShProd(shProd);
                newPic.setProdPic(url);
                pics.add(newPic);
            }
        }
        return pics;
    }
}
