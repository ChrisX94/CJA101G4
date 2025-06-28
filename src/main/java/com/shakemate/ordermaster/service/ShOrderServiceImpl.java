package com.shakemate.ordermaster.service;


import com.shakemate.ordermaster.dao.ShOrderRepository;
import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.ordermaster.util.ShOrderSpecifications;
import com.shakemate.shshop.dto.ShProdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ShOrderServiceImpl implements ShOrderService{

    @Autowired
    private ShOrderRepository orderRepository;


    //  getAll
    public List<ShOrderDto> getAllOrders() {
        List<ShOrder> orders = orderRepository.findAll();
        return orders.stream().map(ShOrderDto::new).collect(Collectors.toList());
    }

    @Override
    public ShOrder createOrder(ShOrder order) {
        return orderRepository.save(order);
    }

    @Override
    public ShOrderDto getOrderById(Integer orderId) {
        Optional<ShOrder> rowOrder = orderRepository.findById(orderId);
        ShOrder order = rowOrder.orElseThrow(() -> new IllegalArgumentException("訂單不存在"));;
        ShOrderDto dto = new ShOrderDto(order);
        return dto;
    }

    @Override
    public List<ShOrderDto> getOrdersByBuyer(Integer buyerUserId) {
        List<ShOrder> orders = orderRepository.findByBuyerUserId(buyerUserId);
        return orders.stream().map(ShOrderDto::new).collect(Collectors.toList());
    }

    @Override
    public List<ShOrderDto> getOrdersBySeller(Integer sellerUserId) {
        List<ShOrder> orders = orderRepository.findBySellerUserId(sellerUserId);
        return orders.stream().map(ShOrderDto::new).collect(Collectors.toList());
    }

    @Override
    public List<ShOrderDto> getOrdersByProd(Integer prodId) {
        List<ShOrder> orders= orderRepository.findBySellerUserId(prodId);
        return orders.stream().map(ShOrderDto::new).collect(Collectors.toList());
    }

    @Override
    public ShOrder updateOrder(ShOrder order) {
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<ShOrderDto> searchOrders(
            Integer shOrderId,
            String buyerName,
            String sellerName,
            String prodName,
            Byte orderStatus,
            Timestamp startDate,
            Timestamp endDate){
        Specification<ShOrder> spec = Specification
                .where(ShOrderSpecifications.hasOrderId(shOrderId))
                .and(ShOrderSpecifications.hasBuyerName(buyerName))
                .and(ShOrderSpecifications.hasSellerName(sellerName))
                .and(ShOrderSpecifications.hasProductName(prodName))
                .and(ShOrderSpecifications.hasOrderStatus(orderStatus))
                .and(ShOrderSpecifications.orderDateBetween(startDate, endDate));
        List<ShOrder> orders =  orderRepository.findAll(spec);
        return orders.stream().map(ShOrderDto::new).collect(Collectors.toList());
    }

}
