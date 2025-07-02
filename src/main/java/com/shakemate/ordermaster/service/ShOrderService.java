package com.shakemate.ordermaster.service;

import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.dto.ShOrderRequestDto;
import com.shakemate.ordermaster.eum.PaymentStatus;
import com.shakemate.ordermaster.eum.ShippingStatus;
import com.shakemate.ordermaster.model.ShOrder;


import java.sql.Timestamp;
import java.util.List;

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

    ShOrderDto updateOrder(ShOrderRequestDto requestDto);

    void logisticStatus(Integer orderId, ShippingStatus status);

    void paymentStatus(Integer orderId, PaymentStatus status);

    void checkingStatus(Integer orderId);

    void cancelOrder(Integer orderId);
}
