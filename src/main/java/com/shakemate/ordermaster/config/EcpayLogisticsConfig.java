package com.shakemate.ordermaster.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 綠界物流設定
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ecpay.logistics")
public class EcpayLogisticsConfig {
    private String merchantId;
    private String hashKey;
    private String hashIv;
    private String mapUrl;
    private String mapUrl2;
    private String serverReplyUrl;
}