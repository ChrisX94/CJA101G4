package com.shakemate.ordermaster.service;

import com.shakemate.ordermaster.config.EcpayLogisticsConfig;
import com.shakemate.ordermaster.config.EcpayPaymentConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EcpayService {

    @Autowired
    private EcpayPaymentConfig paymentConfig;

    @Autowired
    private EcpayLogisticsConfig logisticsConfig;

    // ✅ 金流專用 CheckMacValue
    public String generateCheckMacValueForPayment(Map<String, String> params) {
        return generateCheckMacValue(params, paymentConfig.getHashKey(), paymentConfig.getHashIv());
    }

    // ✅ 物流專用 CheckMacValue
    public String generateCheckMacValueForLogistics(Map<String, String> params) {
        return generateCheckMacValue(params, logisticsConfig.getHashKey(), logisticsConfig.getHashIv());
    }

    // ✅ 通用 CheckMacValue 產生器
    private String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIv) {

        // 1. 排序、去掉 CheckMacValue
        Map<String,String> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        params.forEach((k,v) -> {
            if (!"CheckMacValue".equalsIgnoreCase(k)) sorted.put(k, v);
        });

        // 2. 先拼完整串（這裡 **不要** encode）
        StringBuilder raw = new StringBuilder();
        raw.append("HashKey=").append(hashKey);
        sorted.forEach((k,v) -> raw.append("&").append(k).append("=").append(v));
        raw.append("&HashIV=").append(hashIv);

        // 3. **整串** URL Encode → 再整串 toLowerCase()
        String encoded = ecpayUrlEncode(raw.toString()).toLowerCase();
        System.out.println("encode → " + encoded);

        // 4. SHA-256 → Hex 大寫
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(encoded.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) hex.append(String.format("%02X", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 綠界專用的 URL 編碼方法
    // 參數值先進行標準 URLEncoder.encode，再替換特定字元，最後轉為小寫
    private String ecpayUrlEncode(String value) {
        System.out.println("DEBUG: Encoding original value: " + value);
        try {
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());


            // 綠界特殊符號不編碼 (這些在標準 URLEncoder.encode 後會變成 %XX，需要替換回來)
            encoded = encoded.replace("%21", "!");
            encoded = encoded.replace("%28", "(");
            encoded = encoded.replace("%29", ")");
            encoded = encoded.replace("%2A", "*");
            encoded = encoded.replace("%2D", "-");
            encoded = encoded.replace("%2E", ".");
            encoded = encoded.replace("%5F", "_");
            System.out.println("DEBUG: After replace special chars: " + encoded);

            // 最後轉為小寫 (這是針對編碼結果中的十六進位字母，不是整個字串)
            return encoded.toLowerCase(); // <--- 這裡是關鍵，只對編碼結果中的 A-F 轉小寫
        } catch (Exception e) {
            throw new RuntimeException("Ecpay URL Encoding Error", e);
        }
    }
}