package com.shakemate.ordermaster.eum;

public enum PaymentMethod {
    COD((byte) 0, "貨到付款"),
    TRANSFER((byte) 1, "線上付款");

    private final byte code;
    private final String label;

    PaymentMethod(byte code, String label) {
        this.code = code;
        this.label = label;
    }

    public byte getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentMethod fromCode(byte code) {
        for (PaymentMethod pm : values()) {
            if (pm.code == code) {
                return pm;
            }
        }
        return null;
    }
}
