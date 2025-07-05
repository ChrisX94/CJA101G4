package com.shakemate.ordermaster.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
public class ShOrderRequestDto {
    private Integer shOrderId;
    private Integer prodId;
    private Integer buyerUserId;
    private Integer sellerUserId;
    private Integer productPrice;
    private Integer productQuantity;
    private Integer platformFee;
    private Integer shippingFee;
    private String shippingAddress;
    private String shippingMethod;
    private byte paymentMethod;
    private String orderNote;
    private Integer totalAmount;
    private byte orderStatus;
    private byte paymentStatus;
    private byte shippingStatus;


    public ShOrderRequestDto(Integer shOrderId, Integer prodId, Integer buyerUserId, Integer sellerUserId, Integer productPrice, Integer productQuantity, Integer platformFee, Integer shippingFee, String shippingAddress, String shippingMethod, byte paymentMethod, String orderNote ) {
        this.shOrderId = shOrderId;
        this.prodId = prodId;
        this.buyerUserId = buyerUserId;
        this.sellerUserId = sellerUserId;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.platformFee = platformFee;
        this.shippingFee = shippingFee;
        this.shippingAddress = shippingAddress;
        this.shippingMethod = shippingMethod;
        this.paymentMethod = paymentMethod;
        this.orderNote = orderNote;
        this.totalAmount = calculateTotalAmount(productPrice,productQuantity,shippingFee);

    }

    public int calculateTotalAmount(int productPrice, int productQuantity, int shippingFee) {
        int subtotal = productPrice * productQuantity;
        int platformFee = (int) Math.ceil(subtotal * 0.01);
        return subtotal + shippingFee + platformFee;
    }



}
