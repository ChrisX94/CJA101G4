package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.eum.PaymentStatus;
import com.shakemate.ordermaster.eum.ShippingStatus;
import com.shakemate.ordermaster.model.ShOrder;
import com.shakemate.ordermaster.dao.ShOrderRepository;
import com.shakemate.user.model.Users;
import com.shakemate.user.dao.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 訂單通知AOP切面
 * 
 * 透過AOP機制攔截訂單相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 訂單創建成功
 * - 訂單付款成功  
 * - 訂單出貨
 * - 訂單送達
 * 
 * 注意：使用@AfterReturning確保業務操作成功後才發送通知，
 * 但需要注意事務提交順序，建議與Spring事件監聽方案配合使用以確保事務安全性。
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OrderNotificationAspect {

    private final NotificationService notificationService;
    private final ShOrderRepository orderRepository;
    private final UsersRepository usersRepository;

    /**
     * 定義切點：攔截訂單服務的createOrder方法
     * 切點表達式說明：
     * - execution(): 執行方法攔截
     * - com.shakemate.ordermaster.service.*Service.createOrder(..): 
     *   攔截ordermaster.service包下所有Service類的createOrder方法
     */
    @Pointcut("execution(* com.shakemate.ordermaster.service.*Service.createOrder(..))")
    public void orderCreationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截訂單服務的paymentStatus方法
     */
    @Pointcut("execution(* com.shakemate.ordermaster.service.*Service.paymentStatus(..))")
    public void orderPaymentPointcut() {
        // 切點定義，無需實現  
    }

    /**
     * 定義切點：攔截訂單服務的logisticStatus方法
     */
    @Pointcut("execution(* com.shakemate.ordermaster.service.*Service.logisticStatus(..))")
    public void orderShippingPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 訂單創建成功後的通知處理
     * 
     * @param result 方法返回結果（ShOrderDto）
     */
    @AfterReturning(pointcut = "orderCreationPointcut()", returning = "result")
    public void handleOrderCreated(Object result) {
        try {
            if (result instanceof ShOrderDto) {
                ShOrderDto orderDto = (ShOrderDto) result;
                
                log.info("AOP攔截到訂單創建成功，訂單ID: {}, 買家ID: {}", 
                        orderDto.getShOrderId(), orderDto.getBuyerId());

                // 準備通知模板變數
                Map<String, Object> templateVariables = buildOrderCreatedVariables(orderDto);
                
                // 發送訂單創建通知給買家
                notificationService.sendTemplateNotification(
                    "ORDER_CREATED", 
                    orderDto.getBuyerId(), 
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送訂單創建通知失敗，訂單ID: {}, 買家ID: {}", 
                            orderDto.getShOrderId(), orderDto.getBuyerId(), throwable);
                    return false;
                });
                
                log.info("訂單創建通知已發送，訂單ID: {}", orderDto.getShOrderId());
                
            } else {
                log.warn("AOP攔截到createOrder方法返回值類型不正確: {}", 
                        result != null ? result.getClass().getSimpleName() : "null");
            }
            
        } catch (Exception e) {
            log.error("處理訂單創建通知時發生錯誤", e);
            // 不拋出異常，避免影響主要業務流程
        }
    }

    /**
     * 訂單付款狀態更新後的通知處理
     * 
     * 注意：此方法攔截paymentStatus方法，需要通過參數判斷具體的付款狀態
     */
    @AfterReturning("orderPaymentPointcut() && args(orderId, status)")
    public void handleOrderPaymentStatus(Integer orderId, Object status) {
        try {
            log.info("AOP攔截到訂單付款狀態更新，訂單ID: {}, 狀態: {}", orderId, status);
            
            // 判斷是否為付款成功狀態
            if (isPaymentSuccessStatus(status)) {
                // 查詢訂單詳細資訊
                ShOrder order = orderRepository.findById(orderId).orElse(null);
                if (order == null) {
                    log.warn("無法找到訂單數據: orderId={}", orderId);
                    return;
                }
                
                log.info("檢測到訂單付款成功，準備發送付款確認通知，訂單ID: {}", orderId);
                
                // 準備付款成功通知模板變數
                Map<String, Object> templateVariables = buildPaymentSuccessVariables(order);
                
                // 發送付款成功通知給買家
                notificationService.sendTemplateNotification(
                    "ORDER_PAYMENT_SUCCESS",
                    order.getBuyer().getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送付款成功通知失敗，訂單ID: {}, 買家ID: {}", 
                            orderId, order.getBuyer().getUserId(), throwable);
                    return false;
                });

                // 發送新訂單通知給賣家
                Map<String, Object> sellerVariables = buildNewOrderSellerVariables(order);
                notificationService.sendTemplateNotification(
                    "ORDER_NEW_SELLER",
                    order.getSeller().getUserId(),
                    sellerVariables
                ).exceptionally(throwable -> {
                    log.error("發送新訂單賣家通知失敗，訂單ID: {}, 賣家ID: {}", 
                            orderId, order.getSeller().getUserId(), throwable);
                    return false;
                });

                log.info("付款成功通知已發送，訂單ID: {}", orderId);
            }
            
        } catch (Exception e) {
            log.error("處理訂單付款通知時發生錯誤，訂單ID: {}", orderId, e);
        }
    }

    /**
     * 訂單物流狀態更新後的通知處理
     */
    @AfterReturning("orderShippingPointcut() && args(orderId, status)")
    public void handleOrderShippingStatus(Integer orderId, Object status) {
        try {
            log.info("AOP攔截到訂單物流狀態更新，訂單ID: {}, 狀態: {}", orderId, status);
            
            // 查詢訂單詳細資訊
            ShOrder order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                log.warn("無法找到訂單數據: orderId={}", orderId);
                return;
            }
            
            // 判斷具體的物流狀態並發送對應通知
            if (isShippedStatus(status)) {
                log.info("檢測到訂單已出貨，準備發送出貨通知，訂單ID: {}", orderId);
                
                // 準備出貨通知模板變數
                Map<String, Object> templateVariables = buildShippedVariables(order);
                
                // 發送出貨通知給買家
                notificationService.sendTemplateNotification(
                    "ORDER_SHIPPED",
                    order.getBuyer().getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送出貨通知失敗，訂單ID: {}, 買家ID: {}", 
                            orderId, order.getBuyer().getUserId(), throwable);
                    return false;
                });

                log.info("出貨通知已發送，訂單ID: {}", orderId);
                
            } else if (isDeliveredStatus(status)) {
                log.info("檢測到訂單已送達，準備發送送達通知，訂單ID: {}", orderId);
                
                // 準備送達通知模板變數
                Map<String, Object> templateVariables = buildDeliveredVariables(order);
                
                // 發送送達通知給買家
                notificationService.sendTemplateNotification(
                    "ORDER_DELIVERED",
                    order.getBuyer().getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送送達通知失敗，訂單ID: {}, 買家ID: {}", 
                            orderId, order.getBuyer().getUserId(), throwable);
                    return false;
                });

                log.info("送達通知已發送，訂單ID: {}", orderId);
            }
            
        } catch (Exception e) {
            log.error("處理訂單物流通知時發生錯誤，訂單ID: {}", orderId, e);
        }
    }

    /**
     * 構建訂單創建通知的模板變數
     */
    private Map<String, Object> buildOrderCreatedVariables(ShOrderDto orderDto) {
        Map<String, Object> variables = new HashMap<>();
        
        // 基本訂單資訊
        variables.put("order_id", orderDto.getShOrderId().toString());
        variables.put("product_name", orderDto.getShProd() != null && orderDto.getShProd().getProdName() != null 
                ? orderDto.getShProd().getProdName() : "商品");
        variables.put("amount", orderDto.getTotalAmount());
        
        // 付款期限（一般為24小時後）
        LocalDateTime paymentDeadline = LocalDateTime.now().plusHours(24);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        variables.put("payment_deadline", paymentDeadline.format(formatter));
        
        // 買家資訊
        variables.put("buyer_name", orderDto.getBuyerName() != null ? orderDto.getBuyerName() : "會員");
        
        log.debug("構建訂單創建通知變數: {}", variables);
        return variables;
    }

    /**
     * 構建付款成功通知的模板變數
     */
    private Map<String, Object> buildPaymentSuccessVariables(ShOrder order) {
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("order_id", order.getShOrderId().toString());
        variables.put("product_name", order.getShProd() != null ? order.getShProd().getProdName() : "商品");
        variables.put("amount", order.getTotalAmount());
        variables.put("buyer_name", order.getBuyer().getUsername());
        variables.put("payment_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        variables.put("shipping_address", order.getShippingAddress());
        
        return variables;
    }

    /**
     * 構建新訂單賣家通知的模板變數
     */
    private Map<String, Object> buildNewOrderSellerVariables(ShOrder order) {
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("order_id", order.getShOrderId().toString());
        variables.put("product_name", order.getShProd() != null ? order.getShProd().getProdName() : "商品");
        variables.put("amount", order.getTotalAmount());
        variables.put("buyer_name", order.getBuyer().getUsername());
        variables.put("seller_name", order.getSeller().getUsername());
        variables.put("quantity", order.getProductQuantity());
        variables.put("order_time", order.getOrderDate().toString());
        
        return variables;
    }

    /**
     * 構建出貨通知的模板變數
     */
    private Map<String, Object> buildShippedVariables(ShOrder order) {
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("order_id", order.getShOrderId().toString());
        variables.put("product_name", order.getShProd() != null ? order.getShProd().getProdName() : "商品");
        variables.put("buyer_name", order.getBuyer().getUsername());
        variables.put("shipping_address", order.getShippingAddress());
        variables.put("shipping_method", getShippingMethodDescription(order.getShippingMethod()));
        variables.put("shipped_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        
        // 為模板提供追蹤號碼和預計送達時間
        // 如果訂單實體中沒有這些欄位，提供預設值
        variables.put("tracking_number", "TW" + order.getShOrderId() + "00" + System.currentTimeMillis() % 10000);
        
        // 預計送達時間：宅配通常1-3天，超商取貨通常2-5天
        LocalDateTime estimatedDelivery;
        if (order.getShippingMethod() != null && order.getShippingMethod() == 1) {
            // 超商取貨：2-5天
            estimatedDelivery = LocalDateTime.now().plusDays(3);
        } else {
            // 宅配：1-3天
            estimatedDelivery = LocalDateTime.now().plusDays(2);
        }
        variables.put("estimated_delivery", estimatedDelivery.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        return variables;
    }

    /**
     * 構建送達通知的模板變數
     */
    private Map<String, Object> buildDeliveredVariables(ShOrder order) {
        Map<String, Object> variables = new HashMap<>();
        
        variables.put("order_id", order.getShOrderId().toString());
        variables.put("product_name", order.getShProd() != null ? order.getShProd().getProdName() : "商品");
        variables.put("buyer_name", order.getBuyer().getUsername());
        variables.put("delivered_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        variables.put("rating_reminder", "別忘了為這次購物體驗評分！");
        
        return variables;
    }

    /**
     * 判斷是否為付款成功狀態
     */
    private boolean isPaymentSuccessStatus(Object status) {
        // 根據PaymentStatus枚舉或狀態碼判斷
        // 假設PAID狀態的code為1
        if (status == null) return false;
        
        if (status instanceof Number) {
            return ((Number) status).intValue() == 1; // PaymentStatus.PAID.getCode()
        }
        
        return false;
    }

    /**
     * 判斷是否為已出貨狀態
     */
    private boolean isShippedStatus(Object status) {
        // 根據ShippingStatus枚舉判斷
        if (status == null) return false;
        
        if (status instanceof Number) {
            return ((Number) status).intValue() == 1; // 假設SHIPPED狀態的code為1
        }
        
        return false;
    }

    /**
     * 判斷是否為已送達狀態
     */
    private boolean isDeliveredStatus(Object status) {
        // 根據ShippingStatus枚舉判斷
        if (status == null) return false;
        
        if (status instanceof Number) {
            return ((Number) status).intValue() == 2; // 假設DELIVERED狀態的code為2
        }
        
        return false;
    }

    /**
     * 獲取運送方式描述
     */
    private String getShippingMethodDescription(Byte shippingMethod) {
        if (shippingMethod == null) {
            return "宅配";
        }
        
        return switch (shippingMethod) {
            case 0 -> "宅配";
            case 1 -> "超商取貨";
            default -> "宅配";
        };
    }
} 