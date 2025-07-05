package com.shakemate.ordermaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 綠界金流設定
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ecpay.payment")
public class EcpayPaymentConfig {
    private String merchantId;
    private String hashKey;
    private String hashIv;
    private String apiUrl;
    private String returnUrl;
    private String clientBackUrl;
}