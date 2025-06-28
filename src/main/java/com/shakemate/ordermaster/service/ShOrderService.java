package com.shakemate.ordermaster.service;

import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.shshop.dto.ShProdDto;


import java.util.List;
import java.util.Optional;

public interface ShOrderService {

    public List<ShOrderDto> getAllOrders();

    ShOrder createOrder(ShOrder order);

    ShOrderDto getOrderById(Integer orderId);

    List<ShOrderDto> getOrdersByBuyer(Integer buyerUserId);

    List<ShOrderDto> getOrdersBySeller(Integer sellerUserId);

    List<ShOrderDto> getOrdersByProd(Integer prodId);

    ShOrder updateOrder(ShOrder order);

}
