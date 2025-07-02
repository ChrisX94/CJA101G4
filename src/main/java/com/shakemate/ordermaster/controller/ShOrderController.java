package com.shakemate.ordermaster.controller;


import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.dto.ShOrderRequestDto;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.ordermaster.service.ShOrderService;
import com.shakemate.shshop.dto.ApiResponse;
import com.shakemate.shshop.dto.ApiResponseFactory;
import com.shakemate.user.model.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
// only for testing purpose, remove this line before deploy to production server

@RestController
@RequestMapping("/api/shorder")
public class ShOrderController {
    @Autowired
    private ShOrderService orderService;

    // 查全部
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ShOrderDto>>> getAllOrders() {
        List<ShOrderDto> data = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 下單
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ShOrderDto>> createOrder(@RequestBody ShOrderRequestDto orderInfo,
                                                               HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer userId = Integer.parseInt(userIdObj.toString());
        orderInfo.setBuyerUserId(userId);
        ShOrderDto data = orderService.createOrder(orderInfo);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 查詢單一訂單
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShOrderDto>> getOrder(@PathVariable Integer id) {
        ShOrderDto data = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 查詢買家的所有訂單
    @GetMapping("/buyer")
    public ResponseEntity<ApiResponse<List<ShOrderDto>>> getOrdersByBuyer(HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer buyerId = Integer.parseInt(userIdObj.toString());
        List<ShOrderDto> data = orderService.getOrdersByBuyer(buyerId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 查詢賣家的所有訂單
    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<List<ShOrderDto>>> getOrdersBySeller(HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer sellerId = Integer.parseInt(userIdObj.toString());
        List<ShOrderDto> data = orderService.getOrdersBySeller(sellerId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 查詢某個商品的訂單
    @GetMapping("/prod/{prodId}")
    public ResponseEntity<ApiResponse<List<ShOrderDto>>> getOrdersByProd(@PathVariable Integer prodId) {
        List<ShOrderDto> data = orderService.getOrdersByProd(prodId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 更新訂單（例如付款、出貨、取消）
    @PostMapping("/update")
    public ShOrderDto updateOrder(@RequestBody ShOrderRequestDto requestDto) {
        return orderService.updateOrder(requestDto);
    }

    // 取消訂單
    @PostMapping("/cancel")
    public void cancelOrder(@RequestParam Integer shOrderId){
        orderService.cancelOrder(shOrderId);
    }


    // 複合條件查詢 (orderId, buyerName, sellerName, prodName, orderStatus, date range)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ShOrderDto>>> searchOrders(
            @RequestParam(value = "shOrderId", required = false) Integer shOrderId,
            @RequestParam(value = "buyerName", required = false) String buyerName,
            @RequestParam(value = "sellerName", required = false) String sellerName,
            @RequestParam(value = "prodName", required = false) String prodName,
            @RequestParam(value = "orderStatus", required = false) Byte orderStatus,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<ShOrderDto> orders = orderService.searchOrders(
                shOrderId,
                buyerName,
                sellerName,
                prodName,
                orderStatus,
                startDate != null ? Timestamp.valueOf(startDate) : null,
                endDate != null ? Timestamp.valueOf(endDate) : null
        );
        return ResponseEntity.ok(ApiResponseFactory.success(orders));
    }

}