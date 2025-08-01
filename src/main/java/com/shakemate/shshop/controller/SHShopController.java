package com.shakemate.shshop.controller;

import com.shakemate.shshop.dto.*;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdType;
import com.shakemate.shshop.service.ShShopService;
import com.shakemate.shshop.util.OpenAiAPI;
import com.shakemate.shshop.util.PostMultipartFileUploader;
import com.shakemate.shshop.util.ShShopRedisUtil;
import com.shakemate.user.dto.UserDto;
import com.shakemate.util.PostImageUploader;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    @Autowired
    private ShShopRedisUtil redisUtil;


    // 取得聊天室的網址
    @GetMapping("/getRoomUrl")
    public ResponseEntity<ApiResponse<String>> getRoomUrl(@RequestParam("seller") Integer seller, HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer viewerId = Integer.parseInt(userIdObj.toString());
        Integer roomId = shShopService.getRoomId(viewerId, seller);
        String url = "/match_chatroom/chatroom.html?roomId=" + roomId;
        return ResponseEntity.ok(ApiResponseFactory.success(url));

    }


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
    ) {
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
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        }
    }

    // 取得全部好友商品(會員用)
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
                for(ShProdDto p : data){
                    String reason = "RejectionProdId_" + p.getProdId().toString();
                    p.setRejectReason(redisUtil.get(reason));
                }
                break;
            case "available":
                data = data.stream().filter(p -> p.getProdStatus() == 2).collect(Collectors.toList());
                break;
            case "notAvailable":
                data = data.stream().filter(p -> p.getProdStatus() == 3).collect(Collectors.toList());
                break;
            default:
                for(ShProdDto p : data){
                    String reason = "RejectionProdId_" + p.getProdId().toString();
                    p.setRejectReason(redisUtil.get(reason));
                }
                break;
        }
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 用PK找商品(get) 會計數
    @GetMapping("/getProd")
    public ResponseEntity<ApiResponse<ShProdDto>> getProdsById(@RequestParam("id") Integer id) {
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

    // 用PK找商品(post)用於新增及修改不會計數
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<ShProdDto>> getProdForBuy(@RequestParam("id") String idStr) {
        Integer id = Integer.parseInt(idStr);
        ShProdDto data = shShopService.getByIdForBuy(id);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }
    // 用user找商品(會員用)
    @PostMapping("/getProdsByUser")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getAvailableProdsByUser(@RequestParam("userId") String userIdStr) {
        Integer userId = Integer.parseInt(userIdStr.toString());
        List<ShProdDto> data = shShopService.getAvailableProdsByUser(userId);
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
            @RequestParam(value = "picUrls", required = false) List<String> OriginalPicUrls,
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
        if (OriginalPicUrls != null && OriginalPicUrls.size() > 0) {
            picUrls.addAll(OriginalPicUrls);
        }
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


    // 會員下架商品(用session 去綁定)
    @PostMapping("/delist")
    public ResponseEntity<ApiResponse<ShProdDto>> delistProdByUser(@RequestParam("prodId") Integer prodId,
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

    @PostMapping("/sendReview")
    public ResponseEntity<ApiResponse<ShProdDto>> sendReviewByUser(@RequestParam("prodId") Integer prodId,
                                                               HttpSession session) {
        Object userIdObj = session.getAttribute("account");
        if (userIdObj == null) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseFactory.error(400, "尚未登入"));
        }
        Integer userId = Integer.parseInt(userIdObj.toString());
        ShProdDto data = shShopService.sendReviewProdByUser(userId, prodId);

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
            return ResponseEntity.ok(ApiResponseFactory.success("查無此商品", data));
        } else {
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

    // 用user找商品(管理員用)
    @PostMapping("/getProdsByUserAdm")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByUser(@RequestParam("userId") String userIdStr) {
        Integer userId = Integer.parseInt(userIdStr.toString());
        List<ShProdDto> data = shShopService.getProdsByUser(userId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 修改商品狀態 (尚未導入adm session)
    @PostMapping("/changeStatus")
    public ResponseEntity<ApiResponse<String>> admChangeProdStatus(@RequestParam("prodId") Integer prodId,
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
            case "pending":
                returnMsg = "pending review";
                shShopService.changeProdStatus(prodId, (byte) 0);
                break;
            case "reject": // 強制轉換用
                returnMsg = "Rejected";
                shShopService.changeProdStatus(prodId, (byte) 1);
                break;
            case "approve":
                String msg = shShopService.changeProdStatus(prodId, (byte) 2);
                if (msg != null && "Success".equals(msg)) {
                    String reason = "RejectionProdId_" + prodId;
                    redisUtil.delete(reason);
                    returnMsg = "Approved";
                }else{
                    return ResponseEntity.ok(ApiResponseFactory.error(404 ,msg));
                }
                break;
            case "delist":
                returnMsg = "Delist";
                shShopService.changeProdStatus(prodId, (byte) 3);
                break;
            default:
                returnMsg = "Unknown Status";
                break;
        }
        return ResponseEntity.ok(ApiResponseFactory.success(returnMsg));
    }
    // 取得所有的審核記錄(Excel檔)
    // 審核不通過 (尚未導入adm session)
    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<ShProdDto>> rejection(@RequestParam("id") Integer prodId,
                                                            @RequestParam("reason") String reason,
                                                            HttpSession session) {
        ShProdDto data = shShopService.prodRejection(prodId, reason);
        if (data == null) {
            return ResponseEntity.ok(ApiResponseFactory.success("No Such Product Or Product status is not in pending", null));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success("success", data));
        }
    }

    // Open AI 審核
    @PostMapping("/aiAudit")
    public ResponseEntity<ApiResponse<List<ProdAuditResult>>> aiAudit(){
        List<ShProdDto> pendingList = shShopService.pending();
        if(pendingList == null || pendingList.size() == 0){
            return ResponseEntity.ok(ApiResponseFactory.success("目前沒有要審核的商品", null));
        }else {
            List<ProdAuditResult> aiResult = shShopService.autoAudit(pendingList);
            return ResponseEntity.ok(ApiResponseFactory.success(aiResult));
        }
    }

    // 取的最新的AI審紀錄
    @PostMapping("/aiAuditLatest")
    public ResponseEntity<ApiResponse<List<ProdAuditResult>>> aiAuditLatest(){
        List<ProdAuditResult> data = shShopService.aiAuditHistory();
        if (data == null || data.isEmpty()) {
            return ResponseEntity.ok(ApiResponseFactory.success("目前沒有AI審核紀錄", data));
        } else {
            return ResponseEntity.ok(ApiResponseFactory.success(data));
        }
    }

    // 取得所有AI審紀錄
    @PostMapping("/aiAuditHistoryAll")
    public ResponseEntity<ApiResponse<Map<String, List<ProdAuditResult>>>> getAuditHistoryAll() {
        Map<String, List<ProdAuditResult>> data = shShopService.aiAuditHistoryAll();
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    // 取得商品的審核不通過紀錄
    @PostMapping("/getRejectReason")
    public ResponseEntity<ApiResponse<String>> getRejectReason(@RequestParam Integer id) {
        String returnMsg = shShopService.getRejectReason(id);
        return ResponseEntity.ok(ApiResponseFactory.success(returnMsg));
    }

    @GetMapping("/downloadAuditHistory")
    public ResponseEntity<byte[]> downloadAuditHistory(){
        try {
            byte[] file = shShopService.generateAuditExcel();
            String fileName = URLEncoder.encode(shShopService.generateFileName(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(file);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(("❌ 沒有審核紀錄可以匯出").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("❌ 沒有審核紀錄可以匯出").getBytes(StandardCharsets.UTF_8));
        }
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
