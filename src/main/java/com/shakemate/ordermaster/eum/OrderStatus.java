package com.shakemate.ordermaster.eum;

public enum OrderStatus {
    PROCESSING((byte) 0, "處理中"),
    COMPLETED((byte) 1, "已完成"),
    CANCELLED((byte) 2, "已取消"),
    DISPUTE((byte) 3, "糾紛處理中");

    private final byte code;
    private final String label;

    OrderStatus(byte code, String label) {
        this.code = code;
        this.label = label;
    }

    public byte getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static OrderStatus fromCode(byte code) {
        for (OrderStatus os : values()) {
            if (os.code == code) {
                return os;
            }
        }
        return null;
    }
}
