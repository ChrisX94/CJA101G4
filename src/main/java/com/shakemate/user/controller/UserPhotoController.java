package com.shakemate.user.controller;

import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import com.shakemate.user.util.UserPostMultipartFileUploader;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile/photo")
public class UserPhotoController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserPostMultipartFileUploader imageUploader;

    /**
     * 上傳照片
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 檢查用戶是否登入
            Integer userId = (Integer) session.getAttribute("account");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.badRequest().body(response);
            }

            // 檢查文件是否為空
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "請選擇要上傳的圖片");
                return ResponseEntity.badRequest().body(response);
            }

            // 檢查文件類型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只能上傳圖片文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 獲取用戶資料
            Users user = userService.getUserById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 檢查照片數量限制
            int photoCount = 0;
            if (user.getImg1() != null)
                photoCount++;
            if (user.getImg2() != null)
                photoCount++;
            if (user.getImg3() != null)
                photoCount++;
            if (user.getImg4() != null)
                photoCount++;
            if (user.getImg5() != null)
                photoCount++;

            if (photoCount >= 5) {
                response.put("success", false);
                response.put("message", "最多只能上傳5張照片");
                return ResponseEntity.badRequest().body(response);
            }

            // 上傳圖片到ImgBB
            String imageUrl = imageUploader.uploadImageToImgbb(file);
            if (imageUrl == null) {
                response.put("success", false);
                response.put("message", "圖片上傳失敗");
                return ResponseEntity.badRequest().body(response);
            }

            // 保存圖片URL到資料庫
            if (user.getImg1() == null) {
                user.setImg1(imageUrl);
            } else if (user.getImg2() == null) {
                user.setImg2(imageUrl);
            } else if (user.getImg3() == null) {
                user.setImg3(imageUrl);
            } else if (user.getImg4() == null) {
                user.setImg4(imageUrl);
            } else if (user.getImg5() == null) {
                user.setImg5(imageUrl);
            }

            userService.updateUser(user);

            response.put("success", true);
            response.put("message", "圖片上傳成功");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "圖片上傳失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 刪除照片
     */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Map<String, Object>> deletePhoto(
            @PathVariable String photoId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 檢查用戶是否登入
            Integer userId = (Integer) session.getAttribute("account");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.badRequest().body(response);
            }

            // 獲取用戶資料
            Users user = userService.getUserById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 解析photoId，格式為 "photo-0", "photo-1" 等
            int photoIndex = -1;
            try {
                photoIndex = Integer.parseInt(photoId.replace("photo-", ""));
            } catch (NumberFormatException e) {
                response.put("success", false);
                response.put("message", "無效的照片ID");
                return ResponseEntity.badRequest().body(response);
            }

            // 根據索引刪除對應的照片
            boolean deleted = false;
            switch (photoIndex) {
                case 0:
                    if (user.getImg1() != null) {
                        user.setImg1(null);
                        deleted = true;
                    }
                    break;
                case 1:
                    if (user.getImg2() != null) {
                        user.setImg2(null);
                        deleted = true;
                    }
                    break;
                case 2:
                    if (user.getImg3() != null) {
                        user.setImg3(null);
                        deleted = true;
                    }
                    break;
                case 3:
                    if (user.getImg4() != null) {
                        user.setImg4(null);
                        deleted = true;
                    }
                    break;
                case 4:
                    if (user.getImg5() != null) {
                        user.setImg5(null);
                        deleted = true;
                    }
                    break;
                default:
                    response.put("success", false);
                    response.put("message", "無效的照片索引");
                    return ResponseEntity.badRequest().body(response);
            }

            if (!deleted) {
                response.put("success", false);
                response.put("message", "該位置沒有照片");
                return ResponseEntity.badRequest().body(response);
            }

            // 重新整理照片順序（將後面的照片往前移）
            if (photoIndex < 4) {
                // 將後面的照片往前移
                for (int i = photoIndex; i < 4; i++) {
                    String nextImg = null;
                    switch (i + 1) {
                        case 1 -> nextImg = user.getImg1();
                        case 2 -> nextImg = user.getImg2();
                        case 3 -> nextImg = user.getImg3();
                        case 4 -> nextImg = user.getImg4();
                        case 5 -> nextImg = user.getImg5();
                    }

                    switch (i) {
                        case 0 -> user.setImg1(nextImg);
                        case 1 -> user.setImg2(nextImg);
                        case 2 -> user.setImg3(nextImg);
                        case 3 -> user.setImg4(nextImg);
                        case 4 -> user.setImg5(nextImg);
                    }
                }
                // 最後一個位置設為null
                user.setImg5(null);
            }

            userService.updateUser(user);

            response.put("success", true);
            response.put("message", "照片刪除成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}