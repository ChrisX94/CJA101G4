package com.shakemate.ordermaster.controller;


import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.ordermaster.service.ShOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shorder")
public class ShOrderController {
    @Autowired
    private ShOrderService orderService;

    // 下單
    @PostMapping("/create")
    public ShOrder createOrder(@RequestBody ShOrder order) {
        return orderService.createOrder(order);
    }

    // 查詢單一訂單
    @GetMapping("/{id}")
    public Optional<ShOrder> getOrder(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    // 查詢買家的所有訂單
    @GetMapping("/buyer/{buyerId}")
    public List<ShOrder> getOrdersByBuyer(@PathVariable Integer buyerId) {
        return orderService.getOrdersByBuyer(buyerId);
    }

    // 查詢賣家的所有訂單
    @GetMapping("/seller/{sellerId}")
    public List<ShOrder> getOrdersBySeller(@PathVariable Integer sellerId) {
        return orderService.getOrdersBySeller(sellerId);
    }

    // 查詢某個商品的訂單
    @GetMapping("/prod/{prodId}")
    public List<ShOrder> getOrdersByProd(@PathVariable Integer prodId) {
        return orderService.getOrdersByProd(prodId);
    }

    // 更新訂單（例如付款、出貨、取消）
    @PutMapping("/update")
    public ShOrder updateOrder(@RequestBody ShOrder order) {
        return orderService.updateOrder(order);
    }


}