package com.shakemate.ordermaster.eum;


public enum ShippingStatus {
    PREPARING((byte) 0, "備貨中"),
    SHIPPED((byte) 1, "已出貨"),
    DELIVERED((byte) 2, "已送達");

    private final byte code;
    private final String label;

    ShippingStatus(byte code, String label) {
        this.code = code;
        this.label = label;
    }

    public byte getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ShippingStatus fromCode(byte code) {
        for (ShippingStatus ss : values()) {
            if (ss.code == code) {
                return ss;
            }
        }
        return null;
    }
}


