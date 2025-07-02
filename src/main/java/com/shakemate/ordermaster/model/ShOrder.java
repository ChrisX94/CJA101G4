package com.shakemate.ordermaster.model;

import com.shakemate.shshop.model.ShProd;
import com.shakemate.user.model.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "SH_ORDER")
@Data
public class ShOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SH_ORDER_ID")
    private Integer shOrderId;

    // Buyer
    @ManyToOne
    @JoinColumn(name = "BUYER_USER_ID", nullable = false)
    private Users buyer;

    // Seller
    @ManyToOne
    @JoinColumn(name = "SELLER_USER_ID", nullable = false)
    private Users seller;


    // 商品
    @ManyToOne
    @JoinColumn(name = "PROD_ID", nullable = false)
    private ShProd shProd;

    // 下單時間
    @CreationTimestamp
    @Column(name = "ORDER_DATE", updatable = false)
    private Timestamp orderDate;

    // 商品數量
    @Column(name = "PRODUCT_QUANTITY", nullable = false)
    private Integer productQuantity;

    // 單價（下單當下的價格）
    @Column(name = "PRODUCT_PRICE", nullable = false)
    private Integer productPrice;

    // 訂單總金額
    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Integer totalAmount;

    // 平台手續費
    @Column(name = "PLATFORM_FEE", nullable = false)
    private Integer platformFee = 0;

    // 運費
    @Column(name = "SHIPPING_FEE", nullable = false)
    private Integer shippingFee = 0;

    // 付款方式
    @Column(name = "PAYMENT_METHOD", nullable = false)
    private Byte paymentMethod = 0;
    /*
        0: 貨到付款 (預設)
        1: 轉帳
        2: 信用卡
        3: 信用卡分期
     */

    // 付款狀態
    @Column(name = "PAYMENT_STATUS", nullable = false)
    private Byte paymentStatus = 0;
    /*
        0: 未付款 (預設)
        1: 已付款
        2: 已退款
     */

    // 收件地址
    @Column(name = "SHIPPING_ADDRESS", length = 200, nullable = false)
    private String shippingAddress;

    // 運送方式
    @Column(name = "SHIPPING_METHOD", nullable = false)
    private Byte shippingMethod = 0;
    /*
        0: 宅配 (預設)
        1: 超商取貨
     */

    // 運送狀態
    @Column(name = "SHIPPING_STATUS", nullable = false)
    private Byte shippingStatus = 0;
    /*
        0: 備貨中 (預設)
        1: 已出貨
        2: 已送達
     */

    // 訂單狀態
    @Column(name = "ORDER_STATUS", nullable = false)
    private Byte orderStatus = 0;
    /*
        0: 處理中 (預設)
        1: 已完成
        2: 已取消
        3: 糾紛處理中
     */

    // 訂單備註
    @Column(name = "ORDER_NOTE", length = 800)
    private String orderNote;

    // 更新時間
    @UpdateTimestamp
    @Column(name = "UPDATED_TIME")
    private Timestamp updatedTime;
}
