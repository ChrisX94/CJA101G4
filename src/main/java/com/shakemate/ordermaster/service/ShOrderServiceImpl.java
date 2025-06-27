package com.shakemate.ordermaster.service;


import com.shakemate.ordermaster.dao.ShOrderRepository;
import com.shakemate.ordermaster.model.ShOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShOrderServiceImpl implements ShOrderService{

    @Autowired
    private ShOrderRepository orderRepository;

    @Override
    public ShOrder createOrder(ShOrder order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<ShOrder> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<ShOrder> getOrdersByBuyer(Integer buyerUserId) {
        return orderRepository.findByBuyerUserId(buyerUserId);
    }

    @Override
    public List<ShOrder> getOrdersBySeller(Integer sellerUserId) {
        return orderRepository.findBySellerUserId(sellerUserId);
    }

    @Override
    public List<ShOrder> getOrdersByProd(Integer prodId) {
        return orderRepository.findByShProdProdId(prodId);
    }

    @Override
    public ShOrder updateOrder(ShOrder order) {
        return orderRepository.save(order);
    }

}
