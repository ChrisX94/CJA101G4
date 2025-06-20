package com.shakemate.shshop.eum;

import java.util.HashMap;
import java.util.Map;

public enum ShShopStatus {
    PENDING((byte) 0, "審核中"),
    REJECT((byte) 1, "審核不通過"),
    OPEN((byte) 2, "已上架"),
    CLOSED((byte) 3, "已下架");

    private final byte code;
    private final String label;

    private static final Map<Byte, ShShopStatus> codeMap = new HashMap<>();

    static {
        for (ShShopStatus status : ShShopStatus.values()) {
            codeMap.put(status.code, status);
        }
    }

    ShShopStatus(byte code, String label) {
        this.code = code;
        this.label = label;
    }

    public byte getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ShShopStatus fromCode(byte code) {
        return codeMap.get(code);
    }
}