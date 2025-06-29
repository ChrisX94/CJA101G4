package com.shakemate.ordermaster.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EcpayService {

    private final EcpayConfig config;

    public EcpayService(EcpayConfig config) {
        this.config = config;
    }

    public String generateCheckMacValue(Map<String, String> params) {
        try {
            // 排序參數
            Map<String, String> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sorted.putAll(params);

            // 拼接
            StringBuilder sb = new StringBuilder();
            sb.append("HashKey=").append(config.getHashKey());
            sorted.forEach((k, v) -> sb.append("&").append(k).append("=").append(v));
            sb.append("&HashIV=").append(config.getHashIv());

            String raw = sb.toString();
            System.out.println("✅ 原始拼接字串: " + raw);

            // URL Encode + 特殊處理
            String urlEncoded = urlEncode(raw);
            System.out.println("✅ URL Encode後: " + urlEncoded);

            // SHA256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));

            // 轉16進位大寫
            StringBuilder result = new StringBuilder();
            for (byte b : digest) {
                result.append(String.format("%02X", b));
            }

            System.out.println("✅ 最終CheckMacValue: " + result);
            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue計算失敗: " + e.getMessage(), e);
        }
    }

    // ✔ 符合ECPay官方規範的URL Encode
    private String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace("%21", "!")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%2A", "*")
                    .replace("%2D", "-")
                    .replace("%2E", ".")
                    .replace("%5F", "_")
                    .toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("URL Encode失敗: " + e.getMessage(), e);
        }
    }

    @Data
    @Service
    @ConfigurationProperties(prefix = "ecpay")
    public static class EcpayConfig {
        private String merchantId;
        private String hashKey;
        private String hashIv;
        private String logisticsMapUrl;
        private String serverReplyUrl;
        private String logisticsMapUrl2;
    }
}