package com.shakemate.shshop.service;

import com.google.gson.Gson;
import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.shshop.dao.ShShopTypeRepository;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.dto.ShProdTypeDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Gson gson = new Gson();
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
        Gson gson = new Gson();
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

    @Transactional(readOnly = false)
    public ShProdDto addShProd(ShProd shProd){
        ShProd newProd= repository.save(shProd);
        ShProdDto dto = new ShProdDto(newProd);
        return dto;
    }
}


//
//public class ShShopService {
//    private ShShopDao dao;
//    public ShShopService(){
//        dao = new ShShopImpl();
//    }
////    getProdByUserId
//    public String getProdsByUser(Integer userid){
//        Gson gson = new Gson();
//        List<ShProdDto> dtos = new ArrayList<>();
//        List<ShProd> list = dao.getProdByUserId(userid);
//        for(ShProd p : list){
//            ShProdDto dto = new ShProdDto(p);
//            dtos.add(dto);
//        }
//        return gson.toJson(dtos);
//
//    }
//
//    public String updateProd(Integer prodId, String prodName, Integer ShProdTypeId, String prodContent,
//                             String prodStatusDesc, Integer prodPrice, Integer prodCount, String prodBrand, byte prodStatus, String[] shProdPicUrl){
//        Gson gson = new Gson();
//        ShProd shProd = dao.getOneByPK(prodId);
//        ShProdType shProdType =dao.getProdType(ShProdTypeId);
//        shProd.setProdType(shProdType);
//        shProd.setProdName(prodName);
//        shProd.setProdContent(prodContent);
//        shProd.setProdStatusDesc(prodStatusDesc);
//        shProd.setProdPrice(prodPrice);
//        shProd.setProdBrand(prodBrand);
//        shProd.setProdCount(prodCount);
////        shProd.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
//        shProd.setProdStatus(prodStatus);
//        List<ShProdPic> shProdPic = shProdPic(shProdPicUrl,shProd);
//        shProd.setProdPics(shProdPic);
//        ShProdDto dto =  new ShProdDto(dao.updateProd(shProd));
//        return gson.toJson(dto);
//    }
//
//
//    public String createNewProd(Integer userId, String prodName, Integer ShProdTypeId, String prodContent,
//                                String prodStatusDesc, Integer prodPrice, String prodBrand, Integer prodCount , byte prodStatus, String[] shProdPicUrl) {
//        Gson gson = new Gson();
//        Users user = new Users();
//        user.setUserId(userId);
//
//        ShProdType shProdType =new ShProdType();
//        shProdType.setProdTypeId(ShProdTypeId);
//        ShProd shProd = new ShProd();
//        shProd.setUser(user);
//        shProd.setProdName(prodName);
//        shProd.setProdType(shProdType);
//        shProd.setProdContent(prodContent);
//        shProd.setProdStatusDesc(prodStatusDesc);
//        shProd.setProdPrice(prodPrice);
//        shProd.setProdBrand(prodBrand);
//        shProd.setProdCount(prodCount);
//        shProd.setProdViews(0);
//        shProd.setProdStatus(prodStatus);
//        Timestamp now = new Timestamp(System.currentTimeMillis());
//        shProd.setProdRegTime(now);
//        shProd.setUpdatedTime(now);
//        List<ShProdPic> shProdPic = shProdPic(shProdPicUrl,shProd);
//        shProd.setProdPics(shProdPic);
//        ShProdDto dto =  new ShProdDto(dao.addShProd(shProd));
//        return gson.toJson(dto) ;
//    }
//
//    public String getOneById(Integer id){
//        Gson gson  = new Gson();
//        ShProd shProd = dao.getOneByPK(id);
//        ShProdDto dto = new ShProdDto(shProd); // 呼叫建構子轉換
//        return gson.toJson(dto);
//    }
//
//    public List<ShProd> getAll(){
//
//        return dao.findAll();
//    }
//
//    public String getAllJson() {
//        Gson gson  = new Gson();
//        List<ShProd> list = dao.findAllWithPicsAndType();
//        List<ShProdDto> dtoList = new ArrayList<>();
//        for (ShProd prod : list) {
//            ShProdDto dto = new ShProdDto(prod); // 呼叫建構子轉換
//            dtoList.add(dto);
//        }
//        return gson.toJson(dtoList);
//    }
//
//    public String getAllType(){
//        Gson gson = new Gson();
//        List<ShProdTypeDto> dtoList = new ArrayList<>();
//        List<ShProdType> list = dao.findAllTypes();
//        for(ShProdType type : list ){
//            ShProdTypeDto dto = new ShProdTypeDto(type.getProdTypeId(), type.getProdTypeName() );
//            dtoList.add(dto);
//        }
//        return gson.toJson(dtoList);
//    }
//
//    private List<ShProdPic> shProdPic(String[] shProdPicUrl, ShProd shProd) {
//        List<ShProdPic> pics = shProd.getProdPics();
//        if (pics == null) {
//            // 新增時：原 list 為 null，初始化
//            pics = new ArrayList<>();
//            shProd.setProdPics(pics);
//        } else {
//            // 更新時：清除舊圖（會觸發 Hibernate orphanRemoval 自動刪除）
//            pics.clear();
//        }
//        // 新增新圖（無論是新增或更新）
//        if (shProdPicUrl != null && shProdPicUrl.length > 0) {
//            for (String url : shProdPicUrl) {
//                ShProdPic newPic = new ShProdPic();
//                newPic.setShProd(shProd);
//                newPic.setProdPic(url);
//                pics.add(newPic);
//            }
//        }
//        return pics;
//    }
//}
