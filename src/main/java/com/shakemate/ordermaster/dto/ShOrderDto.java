package com.shakemate.ordermaster.dto;


import com.shakemate.ordermaster.eum.*;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.shshop.dto.ShProdDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;



@Setter
@Getter
@NoArgsConstructor
public class ShOrderDto {

    private Integer shOrderId;
    private Integer buyerId;
    private String buyerName;
    private String buyerEmail;
    private Integer sellerId;
    private String sellerName;
    private String sellerEmail;
    private ShProdDto shProd;
    private Timestamp orderDate;
    private Integer productQuantity;
    private Integer productPrice;
    private Integer totalAmount;
    private Integer platformFee = 0;
    private Integer shippingFee = 0;
    private Byte paymentMethod = 0;
    private String paymentMethodStr;
    private Byte paymentStatus = 0;
    private String paymentStatusStr;
    private String shippingAddress;
    private Byte shippingMethod = 0;
    private String shippingMethodStr;
    private Byte shippingStatus = 0;
    private String shippingStatusStr;
    private Byte orderStatus = 0;
    private String orderStatusStr;
    private String orderNote;
    private Timestamp updatedTime;

    public ShOrderDto(ShOrder shOrder) {
        this.shOrderId = shOrder.getShOrderId();
        this.buyerId = shOrder.getBuyer().getUserId();
        this.buyerName = shOrder.getBuyer().getUsername();
        this.buyerEmail = shOrder.getBuyer().getEmail();
        this.sellerId = shOrder.getSeller().getUserId();
        this.sellerName = shOrder.getSeller().getUsername();
        this.sellerEmail = shOrder.getSeller().getEmail();
        this.shProd = new ShProdDto().forUpdateDisplay(shOrder.getShProd());
        this.orderDate = shOrder.getOrderDate();
        this.productQuantity = shOrder.getProductQuantity();
        this.productPrice = shOrder.getProductPrice();
        this.totalAmount = shOrder.getTotalAmount();
        this.platformFee = shOrder.getPlatformFee();
        this.shippingFee = shOrder.getShippingFee();

        this.paymentMethod = shOrder.getPaymentMethod();
        PaymentMethod pm = PaymentMethod.fromCode(paymentMethod);
        this.paymentMethodStr = pm != null ? pm.getLabel() : "未知";

        this.paymentStatus = shOrder.getPaymentStatus();
        PaymentStatus ps = PaymentStatus.fromCode(paymentStatus);
        this.paymentStatusStr = ps != null ? ps.getLabel() : "未知";

        this.shippingAddress = shOrder.getShippingAddress();

        this.shippingMethod = shOrder.getShippingMethod();
        ShippingMethod sm = ShippingMethod.fromCode(shippingMethod);
        this.shippingMethodStr = sm != null ? sm.getLabel() : "未知";

        this.shippingStatus = shOrder.getShippingStatus();
        ShippingStatus ss = ShippingStatus.fromCode(shippingStatus);
        this.shippingStatusStr = ss != null ? ss.getLabel() : "未知";

        this.orderStatus = shOrder.getOrderStatus();
        OrderStatus os = OrderStatus.fromCode(orderStatus);
        this.orderStatusStr = os != null ? os.getLabel() : "未知";

        this.orderNote = shOrder.getOrderNote();
        this.updatedTime = shOrder.getUpdatedTime();
    }
}

