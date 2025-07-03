package com.shakemate.ordermaster.eum;

public enum PaymentStatus {
    UNPAID((byte) 0, "未付款"),
    PAID((byte) 1, "已付款"),
    REFUNDED((byte) 2, "已退款"),
    CANCELLED((byte) 3, "取消");

    private final byte code;
    private final String label;

    PaymentStatus(byte code, String label) {
        this.code = code;
        this.label = label;
    }

    public byte getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentStatus fromCode(byte code) {
        for (PaymentStatus ps : values()) {
            if (ps.code == code) {
                return ps;
            }
        }
        return null;
    }
}
