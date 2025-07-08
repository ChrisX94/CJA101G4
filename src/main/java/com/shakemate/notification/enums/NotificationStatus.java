package com.shakemate.notification.enums;

/**
 * 通知狀態枚舉類
 * 用於標準化通知狀態管理，替代原有的數字狀態碼
 */
public enum NotificationStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已發布"),
    WITHDRAWN(2, "已撤回"),
    EXPIRED(3, "已過期");
    
    private final int code;
    private final String description;
    
    NotificationStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根據狀態碼獲取對應的枚舉值
     * @param code 狀態碼
     * @return 對應的NotificationStatus枚舉
     */
    public static NotificationStatus fromCode(int code) {
        for (NotificationStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的通知狀態碼: " + code);
    }
} 