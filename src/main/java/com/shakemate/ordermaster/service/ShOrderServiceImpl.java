package com.shakemate.ordermaster.service;


import com.shakemate.ordermaster.dao.ShOrderRepository;
import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.dto.ShOrderRequestDto;
import com.shakemate.ordermaster.eum.OrderStatus;
import com.shakemate.ordermaster.eum.PaymentStatus;
import com.shakemate.ordermaster.eum.ShippingStatus;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.ordermaster.util.ShOrderSpecifications;
import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.service.ShShopService;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
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
    @Autowired
    private ShShopService shShopService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    ShShopRepository shShopRepository;


    //  getAll
    public List<ShOrderDto> getAllOrders() {
        List<ShOrder> orders = orderRepository.findAll();
        return orders.stream().map(ShOrderDto::new).collect(Collectors.toList());
    }

    public void cancelOrder(Integer orderId){
        Optional<ShOrder> rowOrder = orderRepository.findById(orderId);
        ShOrder order = rowOrder.orElseThrow(() -> new IllegalArgumentException("訂單不存在"));
        order.setOrderStatus(OrderStatus.CANCELLED.getCode());
        order.setPaymentStatus(PaymentStatus.CANCELLED.getCode());
        order.setShippingStatus(ShippingStatus.CANCELLED.getCode());
        order.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);

    }

    @Override
    public ShOrderDto createOrder(ShOrderRequestDto orderInfo) {
        ShOrder order = new ShOrder();
        Users buyer = usersRepository.getOne(orderInfo.getBuyerUserId());
        Users seller = usersRepository.getOne(orderInfo.getSellerUserId());
        ShProd prod = shShopRepository.getById(orderInfo.getProdId());
        buyer.setUserId(orderInfo.getBuyerUserId());
        seller.setUserId(orderInfo.getSellerUserId());
        prod.setProdId(orderInfo.getProdId());
        order.setShProd(prod);
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setProductPrice(orderInfo.getProductPrice());
        order.setProductQuantity(orderInfo.getProductQuantity());
        order.setShippingFee(orderInfo.getShippingFee());
        order.setPlatformFee(orderInfo.getPlatformFee());
        order.setShippingAddress(orderInfo.getShippingAddress());
        order.setPaymentMethod(orderInfo.getPaymentMethod());
        order.setOrderNote(orderInfo.getOrderNote());
        int amount = calculateTotalAmount(orderInfo.getProductPrice(), orderInfo.getProductQuantity(), orderInfo.getShippingFee());
        order.setTotalAmount(amount);
        order.setOrderStatus((byte) 0);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        ShOrder savedOrder = orderRepository.save(order);
        ShOrderDto dto = null;
        if(savedOrder != null) {
            dto= new ShOrderDto(savedOrder);
            shShopService.orderCreated(savedOrder.getShProd().getProdId(), savedOrder.getProductQuantity());
        }
        return dto;
    }

    @Override
    public ShOrderDto updateOrder(ShOrderRequestDto requestDto) {
        Optional<ShOrder> rowOrder = orderRepository.findById(requestDto.getShOrderId());
        ShOrder order = rowOrder.orElseThrow(() -> new IllegalArgumentException("訂單不存在"));;
        order.setOrderStatus(requestDto.getOrderStatus());
        order.setPaymentStatus(requestDto.getPaymentStatus());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setShippingStatus(requestDto.getShippingStatus());
        order.setOrderNote(requestDto.getOrderNote());
        order.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        return new ShOrderDto(orderRepository.save(order));
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
    @Transactional
    public void checkingStatus(Integer orderId) {
        Optional<ShOrder> rowOrder = orderRepository.findById(orderId);
        ShOrder order = rowOrder.orElseThrow(() -> new IllegalArgumentException("訂單不存在"));
        if(order.getPaymentStatus() == PaymentStatus.PAID.getCode() && order.getShippingStatus() == ShippingStatus.DELIVERED.getCode()) {
            order.setOrderStatus(OrderStatus.COMPLETED.getCode());
            orderRepository.save(order);
        }

    }

    @Override
    @Transactional
    public void logisticStatus(Integer orderId, ShippingStatus status) {
        Optional<ShOrder> rowOrder = orderRepository.findById(orderId);
        ShOrder order = rowOrder.orElseThrow(() -> new IllegalArgumentException("訂單不存在"));;
        order.setShippingStatus(status.getCode());
        order.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void paymentStatus(Integer orderId, PaymentStatus status) {
        Optional<ShOrder> rowOrder = orderRepository.findById(orderId);
        ShOrder order = rowOrder.orElseThrow(() -> new IllegalArgumentException("訂單不存在"));;
        order.setPaymentStatus(status.getCode());
        order.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
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



    public int calculateTotalAmount(int productPrice, int productQuantity, int shippingFee) {
        int subtotal = productPrice * productQuantity;
        int platformFee = (int) Math.ceil(subtotal * 0.01);
        return subtotal + shippingFee + platformFee;
    }
}
