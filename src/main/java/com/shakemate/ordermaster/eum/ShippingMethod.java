package com.shakemate.ordermaster.eum;

public enum ShippingMethod {
    HOME_DELIVERY((byte) 0, "宅配"),
    STORE_PICKUP((byte) 1, "超商取貨");

    private final byte code;
    private final String label;

    ShippingMethod(byte code, String label) {
        this.code = code;
        this.label = label;
    }

    public byte getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ShippingMethod fromCode(byte code) {
        for (ShippingMethod sm : values()) {
            if (sm.code == code) {
                return sm;
            }
        }
        return null;
    }
}
