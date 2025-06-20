package com.shakemate.shshop.util;

public class ImgbbAK {
    private static final String IMGBB_API_KEY = "46151f177ffa2e0129395cc448b2c190";

    public static String getImgbbApiKey(String password) {
        if ("for_ShShop_Use_Only".equals(password)) {
            return IMGBB_API_KEY;
        }else
            return null;
    }
}
