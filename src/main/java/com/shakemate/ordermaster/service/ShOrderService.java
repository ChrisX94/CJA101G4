package com.shakemate.ordermaster.service;

import com.shakemate.ordermaster.model.ShOrder;


import java.util.List;
import java.util.Optional;

public interface ShOrderService {

    ShOrder createOrder(ShOrder order);

    Optional<ShOrder> getOrderById(Integer orderId);

    List<ShOrder> getOrdersByBuyer(Integer buyerUserId);

    List<ShOrder> getOrdersBySeller(Integer sellerUserId);

    List<ShOrder> getOrdersByProd(Integer prodId);

    ShOrder updateOrder(ShOrder order);

}
