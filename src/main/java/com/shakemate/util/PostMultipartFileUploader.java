package com.shakemate.util;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

@Component
public class PostMultipartFileUploader {
    private static final String IMGBB_API_KEY = "46151f177ffa2e0129395cc448b2c190";

    public static String uploadImageToImgbb(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) return null;

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
