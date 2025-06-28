package com.shakemate.ordermaster.util;


import com.shakemate.ordermaster.model.ShOrder;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;

public class ShOrderSpecifications {

    // 訂單ID（精準）
    public static Specification<ShOrder> hasOrderId(Integer orderId) {
        return (root, query, cb) -> {
            if (orderId == null) return null;
            return cb.equal(root.get("shOrderId"), orderId);
        };
    }

    // 買家名稱（模糊）
    public static Specification<ShOrder> hasBuyerName(String buyerName) {
        return (root, query, cb) -> {
            if (buyerName == null || buyerName.isBlank()) return null;
            return cb.like(root.get("buyer").get("username"), "%" + buyerName + "%");
        };
    }

    // 賣家名稱（模糊）
    public static Specification<ShOrder> hasSellerName(String sellerName) {
        return (root, query, cb) -> {
            if (sellerName == null || sellerName.isBlank()) return null;
            return cb.like(root.get("seller").get("username"), "%" + sellerName + "%");
        };
    }

    // 商品名稱（模糊）
    public static Specification<ShOrder> hasProductName(String prodName) {
        return (root, query, cb) -> {
            if (prodName == null || prodName.isBlank()) return null;
            return cb.like(root.get("shProd").get("prodName"), "%" + prodName + "%");
        };
    }

    // 訂單日期範圍
    public static Specification<ShOrder> orderDateBetween(Timestamp startDate, Timestamp endDate) {
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                return cb.between(root.get("orderDate"), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("orderDate"), startDate);
            } else if (endDate != null) {
                return cb.lessThanOrEqualTo(root.get("orderDate"), endDate);
            } else {
                return null;
            }
        };
    }

    // 訂單狀態
    public static Specification<ShOrder> hasOrderStatus(Byte orderStatus) {
        return (root, query, cb) -> {
            if (orderStatus == null) return null;
            return cb.equal(root.get("orderStatus"), orderStatus);
        };
    }
}
