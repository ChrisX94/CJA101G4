package com.shakemate.ordermaster.service;

import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.dto.ShOrderRequestDto;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.shshop.dto.ShProdDto;


import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ShOrderService {

    List<ShOrderDto> searchOrders(
            Integer shOrderId,
            String buyerName,
            String sellerName,
            String prodName,
            Byte orderStatus,
            Timestamp startDate,
            Timestamp endDate);

    List<ShOrderDto> getAllOrders();

    ShOrderDto createOrder(ShOrderRequestDto orderInfo);

    ShOrderDto getOrderById(Integer orderId);

    List<ShOrderDto> getOrdersByBuyer(Integer buyerUserId);

    List<ShOrderDto> getOrdersBySeller(Integer sellerUserId);

    List<ShOrderDto> getOrdersByProd(Integer prodId);

    ShOrder updateOrder(ShOrder order);

    void markedAsPaid(Integer orderId);

}
