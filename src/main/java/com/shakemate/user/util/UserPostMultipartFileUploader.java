package com.shakemate.user.util;

import com.shakemate.shshop.util.ImgbbAK;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

@Component
public class UserPostMultipartFileUploader {
    private static final String IMGBB_API_KEY = ImgbbAKUser.getImgbbApiKey("for_User_Use_Only");

    public static String uploadImageToImgbb(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty())
            return null;

        // 1. 將圖片讀成 Base64
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // 2. 發送 POST 請求到 Imgbb
        URL url = new URL("https://api.imgbb.com/1/upload");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "key=" + IMGBB_API_KEY + "&image=" + URLEncoder.encode(base64Image, "UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes());
        }

        // 3. 讀取回應內容
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        // 4. 解析 JSON 回傳
        JSONObject jsonObject = new JSONObject(response.toString());

        if (!jsonObject.getBoolean("success")) {
            System.out.println("圖片上傳失敗: " + jsonObject);
            return null;
        }

        return jsonObject.getJSONObject("data").getString("url");
    }
}
