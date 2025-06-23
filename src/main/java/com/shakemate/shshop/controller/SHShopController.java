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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    // 複合查詢
    @PostMapping("/advanceSearch")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> advanceSearch(@RequestParam(value = "prodName", required = false) String prodName,
                                                                      @RequestParam(value = "prodBrand", required = false) String prodBrand,
                                                                      @RequestParam(value = "prodContent", required = false) String prodContent,
                                                                      @RequestParam(value = "typeId", required = false) String typeId,
                                                                      @RequestParam(value = "minPrice", required = false) String minPrice,
                                                                      @RequestParam(value = "maxPrice", required = false) String maxPrice,
                                                                      @RequestParam(value = "username", required = false) String username,
                                                                      HttpSession session
    ){
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Map<String, String> ParamMap = new HashMap();
        Integer userId = Integer.parseInt(userIdObj.toString());
        ParamMap.put("userId", userId.toString());
        ParamMap.put("prodName", prodName);
        ParamMap.put("prodBrand", prodBrand);
        ParamMap.put("prodContent", prodContent);
        ParamMap.put("typeId", typeId);
        ParamMap.put("minPrice", minPrice);
        ParamMap.put("maxPrice", maxPrice);
        ParamMap.put("username", username);
        List<ShProdDto> data = shShopService.getProdsByCompositeQuery(ParamMap);

        if (data == null) {
            return ResponseEntity.ok(ApiResponseFactory.success("No Such Product", null));
        }else{
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        }
    }

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
    // 用user找商品(用戶管理商品用)
    @PostMapping("/myProds")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByUser(@RequestParam("action") String status, HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer userId = Integer.parseInt(userIdObj.toString());
        List<ShProdDto> data = shShopService.getProdsByUser(userId);
        switch (status) {
            case "pending":
                data = data.stream().filter(p -> p.getProdStatus() == 0).collect(Collectors.toList());
                break;
            case "rejected":
                data = data.stream().filter(p -> (p.getProdStatus() == 1)).collect(Collectors.toList());
                break;
            case "OnPending":
                data = data.stream().filter(p -> (p.getProdStatus() == 0 || p.getProdStatus() == 1)).collect(Collectors.toList());
                break;
            case "available":
                data = data.stream().filter(p -> p.getProdStatus() == 2).collect(Collectors.toList());
                break;
            case "notAvailable":
                data = data.stream().filter(p -> p.getProdStatus() == 3).collect(Collectors.toList());
                break;
            default:
                break;
        }
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
            System.out.println(p.getOriginalFilename());
            if (!p.isEmpty()) {
                String url = postImageUploader.uploadImageToImgbb(p);
                System.out.println(url);
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


    // 會員下架商品
    @PostMapping("/delist")
    public ResponseEntity<ApiResponse<ShProdDto>> changeStatus(@RequestParam("prodId") Integer prodId,
                                                               HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer userId = Integer.parseInt(userIdObj.toString());
        ShProdDto data = shShopService.delistProdByUser(userId, prodId);
        if (data == null) {
            return ResponseEntity.ok(ApiResponseFactory.success("No Such Product", null));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", null));
        }
    }

    /* ======================================== Back-End ========================================== */

    // 用id 找商品，管理員用
    @PostMapping("reviewProd")
    public ResponseEntity<ApiResponse<ShProdDto>> reviewProd(@RequestParam("id") Integer prodId, HttpSession session) {

        ShProdDto data = shShopService.getById(prodId);
        if (data == null) {
            return ResponseEntity.ok(ApiResponseFactory.success("查無此商品" , data));
        }else {
            return ResponseEntity.ok(ApiResponseFactory.success(data));
        }
    }

    // 以分類搜尋(管理員用)
    @GetMapping("/getAllProdsByType")
    public ResponseEntity<ApiResponse<ShProdDto>> getAllProdsByType(@RequestParam("id") Integer prodId) {
        ShProdDto data = shShopService.getById(prodId);
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

    // 修改商品狀態
    @PostMapping("/changeStatus")
    public ResponseEntity<ApiResponse<String>> ApproveProd(@RequestParam("prodId") Integer prodId,
                                                           @RequestParam("status") String status,
                                                           HttpSession session) {
//        // 等adm做好在加
//        Object AdmObj = session.getAttribute("Adm");
//        if (userIdObj == null) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(ApiResponseFactory.error(400, "管理員尚未登入"));
//        }
        String returnMsg;
        switch (status) {
            case "pending": returnMsg = "pending review";
                shShopService.changeProdStatus(prodId,(byte)0);
                break;
            case "reject": returnMsg = "Rejected";
                shShopService.changeProdStatus(prodId,(byte)1);
                break;
            case "approve": returnMsg = "Approved";
                shShopService.changeProdStatus(prodId,(byte)2);
                break;
            case "delist": returnMsg = "Delist";
                shShopService.changeProdStatus(prodId,(byte)3);
                break;
            default: returnMsg = "Unknown Status";
                break;
        }
        return ResponseEntity.ok(ApiResponseFactory.success(returnMsg));
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
