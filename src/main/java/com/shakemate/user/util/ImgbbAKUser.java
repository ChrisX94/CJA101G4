package com.shakemate.user.util;

public class ImgbbAKUser {
    private static final String IMGBB_API_KEY = "14f2de95188a5265feb0250833cdfcc9";

    public static String getImgbbApiKey(String password) {
        if ("for_User_Use_Only".equals(password)) {
            return IMGBB_API_KEY;
        } else
            return null;
    }
}
