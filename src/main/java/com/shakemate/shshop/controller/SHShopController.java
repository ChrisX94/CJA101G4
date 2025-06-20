package com.shakemate.shshop.controller;

import com.shakemate.shshop.dto.ApiResponse;
import com.shakemate.shshop.dto.ApiResponseFactory;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.dto.ShProdTypeDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdType;
import com.shakemate.shshop.service.ShShopService;
import com.shakemate.shshop.util.PostMultipartFileUploader;
import com.shakemate.user.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "http://127.0.0.1:5500")
// only for testing purpose, remove this line before deploy to production server
@RestController
@RequestMapping("/api/ShShop")
public class SHShopController {
    @Autowired
    private ShShopService shShopService;
    @Autowired
    private PostMultipartFileUploader postImageUploader;


    /* ======================================== Front-End ========================================== */
    // 取得全部好友商品
    @GetMapping("/getProds")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getAvailableProds(HttpSession session) throws IOException {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }

        List<ShProdDto> list = shShopService.findAvailableProds(Integer.parseInt(userIdObj.toString()));
        if (list == null || list.size() == 0) {
            return ResponseEntity.ok(ApiResponseFactory.success("目前沒有好友有商品，配對連結", list));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", list));
        }
    }

    // 各種排序(會員用)
    @GetMapping("/getProdsOrd")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getAvailableProdsOrd(@RequestParam String ord, HttpSession session) throws IOException {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        List<ShProdDto> list = null;
        switch (ord) {
            case "pa":
                list = shShopService.findAvailableProdsPriceAsc(Integer.parseInt(userIdObj.toString()));
                break;
            case "pd":
                list = shShopService.findAvailableProdsPriceDes(Integer.parseInt(userIdObj.toString()));
                break;
            case "ta":
                list = shShopService.findAvailableProdsTimeAsc(Integer.parseInt(userIdObj.toString()));
                break;
            case "td":
                list = shShopService.findAvailableProdsTimeDes(Integer.parseInt(userIdObj.toString()));
                break;
            default:
                list = shShopService.findAvailableProds(Integer.parseInt(userIdObj.toString()));
                break;
        }
        if (list == null || list.size() == 0) {
            return ResponseEntity.ok(ApiResponseFactory.success("目前沒有好友有商品，配對連結", list));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", list));
        }
    }

    // 以分類搜尋(會員用)
    @GetMapping("/getProdsByType")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByType(@RequestParam("typeId") String typeIdStr, HttpSession session) throws IOException {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        List<ShProdDto> data = shShopService.findAvailableProdsByType(Integer.parseInt(userIdObj.toString()), Integer.parseInt(typeIdStr.toString()));
        if (data == null || data.size() == 0) {
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        }
    }

    // 以關鍵字搜尋(會員用)
    @GetMapping("/keyWord")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdByKey(@RequestParam("keyStr") String keyStr, HttpSession session) throws IOException {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        List<ShProdDto> data = shShopService.findAvailableProdsByStr(Integer.parseInt(userIdObj.toString()), keyStr);
        if (data == null || data.size() == 0) {
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        }
    }

    // 用user找商品(用戶管理商品用)
    @PostMapping("/myProds")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByUser(HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer userId = Integer.parseInt(userIdObj.toString());
        List<ShProdDto> data = shShopService.getProdsByUser(userId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 用PK找商品(get) 會計數
    @GetMapping("/getProd")
    public ResponseEntity<ApiResponse<ShProdDto>> getProdsById(@RequestParam("id") String idStr) {
        Integer id = Integer.parseInt(idStr);
        shShopService.addViews(id);
        ShProdDto data = shShopService.getById(id);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 用PK找商品(post)用於新增及修改不會計數
    @PostMapping("/getProd")
    public ResponseEntity<ApiResponse<ShProdDto>> getProdsByIdPost(@RequestParam("id") String idStr) {
        Integer id = Integer.parseInt(idStr);
        ShProdDto data = shShopService.getByIdForUpdate(id);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 新增商品
    @PostMapping("/addNewProd")
    public ResponseEntity<ApiResponse<ShProdDto>> addNewProd(
            @Valid @ModelAttribute ShProd form,
            @RequestParam("prodTypeId") Integer prodTypeId,
            @RequestParam("prodImage") MultipartFile[] parts,
            HttpSession session) throws IOException {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        ShProdType type = new ShProdType();
        type.setProdTypeId(prodTypeId);
        form.setProdType(type);

        Integer userId = Integer.parseInt(userIdObj.toString());
        List<String> picUrls = new ArrayList<>();
        for (MultipartFile p : parts) {
            if (!p.isEmpty()) {
                String url = postImageUploader.uploadImageToImgbb(p);
                picUrls.add(url);
            }
        }

        ShProdDto saved = shShopService.createNewProduct(userId, form, picUrls);
        return ResponseEntity.ok(ApiResponseFactory.success(saved));
    }

    // 更新商品
    @PostMapping("/updateProd")
    public ResponseEntity<ApiResponse<ShProdDto>> updateProd(
            @Valid @ModelAttribute ShProd form,
            @RequestParam("prodId") Integer prodId,
            @RequestParam("prodTypeId") Integer prodTypeId,
            @RequestParam("prodImage") MultipartFile[] parts,
            HttpSession session) throws IOException {

        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        ShProdType type = new ShProdType();
        type.setProdTypeId(prodTypeId);
        form.setProdType(type);

        Integer userId = Integer.parseInt(userIdObj.toString());
        List<String> picUrls = new ArrayList<>();
        for (MultipartFile p : parts) {
            if (!p.isEmpty()) {
                String url = postImageUploader.uploadImageToImgbb(p);
                picUrls.add(url);
            }
        }
        ShProdDto data = shShopService.updateProd(prodId, userId, form, picUrls);
        if (data == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseFactory.error(403, "無權限修改此商品"));
        }
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    /* ======================================== Back-End ========================================== */

    // 以分類搜尋(管理員用)
    @GetMapping("/getAllProdsByType")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getAllProdsByType(@RequestParam("typeId") String typeIdStr) {
        Integer typeId = Integer.parseInt(typeIdStr);
        List<ShProdDto> data = shShopService.getProdsByType(typeId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 全部商品(管理員用)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getAllShProds() {
        List<ShProdDto> data = shShopService.getAll();
        return ResponseEntity.ok(ApiResponseFactory.success("success", data));
    }

    // 用user找商品(會員、管理員共用)
    @PostMapping("/getProdsByUser")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByUser(@RequestParam("userId") String userIdStr) {
        Integer userId = Integer.parseInt(userIdStr.toString());
        List<ShProdDto> data = shShopService.getProdsByUser(userId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    /* ======================================== General ========================================== */
    // 全部的類別，用於於前端顯示頁面
    @GetMapping("/allTypes")
    public ResponseEntity<ApiResponse<List<ShProdTypeDto>>> getAllType() {
        List<ShProdTypeDto> data = shShopService.getAllType();
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    @GetMapping("/friendList")
    public ResponseEntity<ApiResponse<List<UserDto>>> getFriendsInfo(HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        List<UserDto> list = shShopService.getFriendsInfo(Integer.parseInt(userIdObj.toString()));
        if (list == null || list.size() == 0) {
                return ResponseEntity.ok(ApiResponseFactory.success("目前沒有好友有商品，配對連結", list));
            } else {
                return ResponseEntity.ok(ApiResponseFactory.success("success", list));
            }
        }


}
