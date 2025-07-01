package com.shakemate.ordermaster.dao;


import com.shakemate.ordermaster.model.ShOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShOrderRepository extends JpaRepository<ShOrder, Integer>, JpaSpecificationExecutor<ShOrder> {

    // 根據買家查訂單
    List<ShOrder> findByBuyerUserId(Integer buyerUserId);

    // 根據賣家查訂單
    List<ShOrder> findBySellerUserId(Integer sellerUserId);

    // 根據商品查訂單
    List<ShOrder> findByShProdProdId(Integer prodId);


}
