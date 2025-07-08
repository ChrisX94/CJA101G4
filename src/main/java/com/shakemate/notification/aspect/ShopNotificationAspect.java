package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.user.model.Users;
import com.shakemate.user.dao.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 商店通知AOP切面
 * 
 * 透過AOP機制攔截商店相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 商品創建成功（PRODUCT_CREATED）
 * - 商品審核通過（PRODUCT_APPROVED）
 * - 商品審核拒絕（PRODUCT_REJECTED）
 * - 商品上架（PRODUCT_PUBLISHED）
 * - 商品下架（PRODUCT_UNPUBLISHED）
 * - 商品售完（PRODUCT_SOLD_OUT）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ShopNotificationAspect {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;
    private final ShShopRepository shopRepository;

    /**
     * 定義切點：攔截商品創建操作
     */
    @Pointcut("execution(* com.shakemate.shshop.service.ShShopService.createNewProduct(..))")
    public void productCreationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截商品狀態變更操作
     */
    @Pointcut("execution(* com.shakemate.shshop.service.ShShopService.changeProdStatus(..))")
    public void productStatusChangePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截商品提交審核操作
     */
    @Pointcut("execution(* com.shakemate.shshop.service.ShShopService.sendReviewProdByUser(..))")
    public void productReviewSubmissionPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截訂單創建時商品庫存更新操作
     */
    @Pointcut("execution(* com.shakemate.shshop.service.ShShopService.orderCreated(..))")
    public void productStockUpdatePointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理商品創建後的通知
     * 
     * 當商家成功創建商品後，發送創建成功通知
     * 
     * @param result 創建的商品DTO
     * @param userId 創建用戶ID
     * @param form 商品表單
     * @param picUrls 圖片URL列表
     */
    @AfterReturning(pointcut = "productCreationPointcut() && args(userId, form, picUrls)", returning = "result")
    public void handleProductCreated(Object result, Integer userId, Object form, Object picUrls) {
        try {
            if (result instanceof ShProdDto) {
                ShProdDto productDto = (ShProdDto) result;
                
                log.info("AOP攔截到商品創建成功，商品ID: {}, 用戶ID: {}, 商品名稱: {}", 
                        productDto.getProdId(), userId, productDto.getProdName());

                // 查詢真實的用戶數據
                Users user = usersRepository.findById(userId).orElse(null);
                
                if (user == null) {
                    log.warn("無法找到用戶數據: userId={}", userId);
                    return;
                }

                // 準備通知模板變數 - 使用真實數據
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("product_id", productDto.getProdId());
                templateVariables.put("product_name", productDto.getProdName());
                templateVariables.put("product_brand", productDto.getProdBrand());
                templateVariables.put("product_price", productDto.getProdPrice());
                templateVariables.put("product_type", productDto.getProdTypeName());
                templateVariables.put("seller_name", user.getUsername());
                templateVariables.put("created_time", productDto.getUpdatedTime().toString());

                // 發送商品創建成功通知給商家
                notificationService.sendTemplateNotification(
                    "PRODUCT_CREATED",
                    userId,
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送商品創建通知失敗，商品ID: {}, 用戶ID: {}", productDto.getProdId(), userId, throwable);
                    return false;
                });

                log.info("商品創建通知已發送，商品ID: {}", productDto.getProdId());
            }
            
        } catch (Exception e) {
            log.error("處理商品創建通知時發生錯誤，用戶ID: {}", userId, e);
        }
    }

    /**
     * 處理商品狀態變更後的通知
     * 
     * 當商品狀態變更時，發送對應的通知
     * 
     * @param result 操作結果
     * @param productId 商品ID
     * @param status 新狀態
     */
    @AfterReturning(pointcut = "productStatusChangePointcut() && args(productId, status)", returning = "result")
    public void handleProductStatusChange(Object result, Integer productId, byte status) {
        try {
            if ("Success".equals(result)) {
                log.info("AOP攔截到商品狀態變更，商品ID: {}, 新狀態: {}", productId, status);

                // 查詢商品數據
                ShProd product = shopRepository.getByID(productId);
                if (product == null) {
                    log.warn("無法找到商品數據: productId={}", productId);
                    return;
                }

                // 查詢商家數據
                Users seller = product.getUser();
                if (seller == null) {
                    log.warn("無法找到商家數據: productId={}", productId);
                    return;
                }

                // 根據狀態發送不同的通知
                String templateCode;
                String statusDescription;
                
                switch (status) {
                    case 1: // 審核拒絕
                        templateCode = "PRODUCT_REJECTED";
                        statusDescription = "審核未通過";
                        break;
                    case 2: // 已上架
                        templateCode = "PRODUCT_APPROVED";
                        statusDescription = "審核通過並上架";
                        break;
                    case 3: // 已下架
                        templateCode = "PRODUCT_UNPUBLISHED";
                        statusDescription = "已下架";
                        break;
                    case 4: // 售完
                        templateCode = "PRODUCT_SOLD_OUT";
                        statusDescription = "商品售完";
                        break;
                    default:
                        log.info("商品狀態變更為: {}, 不需要發送通知", status);
                        return;
                }

                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("product_id", productId);
                templateVariables.put("product_name", product.getProdName());
                templateVariables.put("product_brand", product.getProdBrand());
                templateVariables.put("product_price", product.getProdPrice());
                templateVariables.put("product_type", product.getShProdType().getProdTypeName());
                templateVariables.put("seller_name", seller.getUsername());
                templateVariables.put("status_description", statusDescription);
                templateVariables.put("updated_time", product.getUpdatedTime().toString());

                // 發送通知給商家
                notificationService.sendTemplateNotification(
                    templateCode,
                    seller.getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送商品狀態變更通知失敗，商品ID: {}, 狀態: {}", productId, status, throwable);
                    return false;
                });

                log.info("商品狀態變更通知已發送，商品ID: {}, 狀態: {}", productId, status);
            }
            
        } catch (Exception e) {
            log.error("處理商品狀態變更通知時發生錯誤，商品ID: {}, 狀態: {}", productId, status, e);
        }
    }

    /**
     * 處理商品提交審核後的通知
     * 
     * 當商家提交商品審核時，發送提交成功通知
     * 
     * @param result 提交結果
     * @param userId 用戶ID
     * @param productId 商品ID
     */
    @AfterReturning(pointcut = "productReviewSubmissionPointcut() && args(userId, productId)", returning = "result")
    public void handleProductReviewSubmission(Object result, Integer userId, Integer productId) {
        try {
            if (result instanceof ShProdDto) {
                ShProdDto productDto = (ShProdDto) result;
                
                log.info("AOP攔截到商品提交審核，商品ID: {}, 用戶ID: {}", productId, userId);

                // 查詢真實的用戶數據
                Users user = usersRepository.findById(userId).orElse(null);
                
                if (user == null) {
                    log.warn("無法找到用戶數據: userId={}", userId);
                    return;
                }

                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("product_id", productDto.getProdId());
                templateVariables.put("product_name", productDto.getProdName());
                templateVariables.put("product_brand", productDto.getProdBrand());
                templateVariables.put("seller_name", user.getUsername());
                templateVariables.put("submitted_time", productDto.getUpdatedTime().toString());

                // 發送提交審核通知給商家
                notificationService.sendTemplateNotification(
                    "PRODUCT_REVIEW_SUBMITTED",
                    userId,
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送商品提交審核通知失敗，商品ID: {}, 用戶ID: {}", productId, userId, throwable);
                    return false;
                });

                log.info("商品提交審核通知已發送，商品ID: {}", productId);
            }
            
        } catch (Exception e) {
            log.error("處理商品提交審核通知時發生錯誤，用戶ID: {}, 商品ID: {}", userId, productId, e);
        }
    }

    /**
     * 處理商品庫存更新後的通知
     * 
     * 當訂單創建導致商品庫存變更時，如果商品售完則發送通知
     * 
     * @param productId 商品ID
     * @param quantity 購買數量
     */
    @AfterReturning("productStockUpdatePointcut() && args(productId, quantity)")
    public void handleProductStockUpdate(Integer productId, Integer quantity) {
        try {
            log.info("AOP攔截到商品庫存更新，商品ID: {}, 購買數量: {}", productId, quantity);

            // 查詢商品數據
            ShProd product = shopRepository.getByID(productId);
            if (product == null) {
                log.warn("無法找到商品數據: productId={}", productId);
                return;
            }

            // 檢查是否售完
            if (product.getProdCount() == 0 && product.getProdStatus() == 4) {
                // 查詢商家數據
                Users seller = product.getUser();
                if (seller == null) {
                    log.warn("無法找到商家數據: productId={}", productId);
                    return;
                }

                // 準備通知模板變數
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("product_id", productId);
                templateVariables.put("product_name", product.getProdName());
                templateVariables.put("product_brand", product.getProdBrand());
                templateVariables.put("seller_name", seller.getUsername());
                templateVariables.put("sold_out_time", product.getUpdatedTime().toString());

                // 發送售完通知給商家
                notificationService.sendTemplateNotification(
                    "PRODUCT_SOLD_OUT",
                    seller.getUserId(),
                    templateVariables
                ).exceptionally(throwable -> {
                    log.error("發送商品售完通知失敗，商品ID: {}", productId, throwable);
                    return false;
                });

                log.info("商品售完通知已發送，商品ID: {}", productId);
            }
            
        } catch (Exception e) {
            log.error("處理商品庫存更新通知時發生錯誤，商品ID: {}, 數量: {}", productId, quantity, e);
        }
    }
} 