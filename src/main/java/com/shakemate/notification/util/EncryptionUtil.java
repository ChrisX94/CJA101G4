package com.shakemate.notification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密工具類 - 手機號碼加密存儲專用
 * 
 * 提供以下功能：
 * 1. AES加密/解密手機號碼
 * 2. 手機號碼雜湊（用於Redis Key）
 * 3. 手機號碼遮罩顯示
 * 4. 安全的密鑰管理
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Component
public class EncryptionUtil {
    
    // AES 加密演算法
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String HASH_ALGORITHM = "SHA-256";
    
    // 預設加密密鑰 (生產環境應從配置檔案讀取)
    @Value("${notification.phone.encryption.key:ShakeMate2024PhoneEncryptionKey32}")
    private String encryptionKey;
    
    /**
     * AES 加密手機號碼
     * 
     * @param phoneNumber 原始手機號碼
     * @return 加密後的Base64字符串
     * @throws RuntimeException 加密失敗時拋出
     */
    public String encryptPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        
        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(phoneNumber.getBytes("UTF-8"));
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            
            log.debug("手機號碼加密成功，原始長度: {}, 加密後長度: {}", phoneNumber.length(), encrypted.length());
            return encrypted;
            
        } catch (Exception e) {
            log.error("手機號碼加密失敗: {}", e.getMessage(), e);
            throw new RuntimeException("手機號碼加密失敗", e);
        }
    }
    
    /**
     * AES 解密手機號碼
     * 
     * @param encryptedPhone 加密的手機號碼（Base64格式）
     * @return 原始手機號碼
     * @throws RuntimeException 解密失敗時拋出
     */
    public String decryptPhone(String encryptedPhone) {
        if (encryptedPhone == null || encryptedPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("加密手機號碼不能為空");
        }
        
        try {
            SecretKey secretKey = generateSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPhone);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decrypted = new String(decryptedBytes, "UTF-8");
            
            log.debug("手機號碼解密成功，加密長度: {}, 解密後長度: {}", encryptedPhone.length(), decrypted.length());
            return decrypted;
            
        } catch (Exception e) {
            log.error("手機號碼解密失敗: {}", e.getMessage(), e);
            throw new RuntimeException("手機號碼解密失敗", e);
        }
    }
    
    /**
     * 生成手機號碼的雜湊值（用於Redis Key）
     * 
     * @param phoneNumber 原始手機號碼
     * @return SHA-256雜湊值（16進位字符串）
     */
    public String hashPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(phoneNumber.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String hashed = hexString.toString();
            log.debug("手機號碼雜湊成功，原始長度: {}, 雜湊後長度: {}", phoneNumber.length(), hashed.length());
            return hashed;
            
        } catch (Exception e) {
            log.error("手機號碼雜湊失敗: {}", e.getMessage(), e);
            throw new RuntimeException("手機號碼雜湊失敗", e);
        }
    }
    
    /**
     * 手機號碼遮罩顯示（隱私保護）
     * 
     * @param phoneNumber 原始手機號碼
     * @return 遮罩後的手機號碼（如：0912****789）
     */
    public String maskPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "****";
        }
        
        String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
        
        if (cleanPhone.length() < 4) {
            return "****";
        } else if (cleanPhone.length() <= 8) {
            // 短號碼：顯示前2後2
            return cleanPhone.substring(0, 2) + "****" + cleanPhone.substring(cleanPhone.length() - 2);
        } else {
            // 長號碼：顯示前4後3
            return cleanPhone.substring(0, 4) + "****" + cleanPhone.substring(cleanPhone.length() - 3);
        }
    }
    
    /**
     * 驗證手機號碼格式（台灣手機號碼）
     * 
     * @param phoneNumber 手機號碼
     * @return 是否為有效的台灣手機號碼
     */
    public boolean isValidTaiwanMobile(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
        
        // 台灣手機號碼格式：09XXXXXXXX (10位數字)
        return cleanPhone.matches("^09\\d{8}$");
    }
    
    /**
     * 生成密鑰
     */
    private SecretKey generateSecretKey() {
        try {
            // 使用配置的密鑰，確保長度為32字節（256位）
            String key = encryptionKey;
            if (key.length() != 32) {
                // 如果密鑰長度不是32，進行調整
                if (key.length() < 32) {
                    key = key + "0".repeat(32 - key.length());
                } else {
                    key = key.substring(0, 32);
                }
            }
            
            return new SecretKeySpec(key.getBytes("UTF-8"), ALGORITHM);
            
        } catch (Exception e) {
            log.error("生成密鑰失敗: {}", e.getMessage(), e);
            throw new RuntimeException("生成密鑰失敗", e);
        }
    }
    
    /**
     * 生成隨機密鑰（用於初始化）
     * 
     * @return Base64編碼的隨機密鑰
     */
    public static String generateRandomKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("生成隨機密鑰失敗", e);
        }
    }
    
    /**
     * 測試加密解密功能
     * 
     * @param phoneNumber 測試用手機號碼
     * @return 測試結果
     */
    public boolean testEncryption(String phoneNumber) {
        try {
            String encrypted = encryptPhone(phoneNumber);
            String decrypted = decryptPhone(encrypted);
            boolean success = phoneNumber.equals(decrypted);
            
            log.info("加密測試結果: 原始={}, 加密={}, 解密={}, 成功={}", 
                maskPhone(phoneNumber), encrypted.substring(0, Math.min(10, encrypted.length())) + "...", 
                maskPhone(decrypted), success);
            
            return success;
        } catch (Exception e) {
            log.error("加密測試失敗: {}", e.getMessage(), e);
            return false;
        }
    }
} 