package com.shakemate.shshop.controller;


import com.shakemate.shshop.dto.ApiResponse;
import com.shakemate.shshop.dto.ApiResponseFactory;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.dto.ShProdTypeDto;
import com.shakemate.shshop.model.ShProd;
import com.shakemate.shshop.model.ShProdPic;
import com.shakemate.shshop.model.ShProdType;
import com.shakemate.shshop.service.ShShopService;
import com.shakemate.user.model.Users;
import com.shakemate.util.PostMultipartFileUploader;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@CrossOrigin(origins = "http://127.0.0.1:5500") // only for testing purpose, remove this line before deploy to production server
@RestController
@RequestMapping("/api/ShShop")
public class SHShopController {
    @Autowired
    private ShShopService shShopService;
    @Autowired
    private PostMultipartFileUploader postImageUploader;


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getAllShProds() {
        List<ShProdDto> data = shShopService.getAll();
        return ResponseEntity.ok(ApiResponseFactory.success("success", data));
    }

    @GetMapping("/allTypes")
    public ResponseEntity<ApiResponse<List<ShProdTypeDto>>> getAllType(){
        List<ShProdTypeDto> data = shShopService.getAllType();
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    @GetMapping("/getProd")
    public ResponseEntity<ApiResponse<ShProdDto>> getProdsById(@RequestParam("id") String idStr){
        Integer id = Integer.parseInt(idStr);
        ShProdDto data = shShopService.getById(id);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    @PostMapping("/getProd")
    public ResponseEntity<ApiResponse<ShProdDto>> getProdsByIdPost(@RequestParam("id") String idStr){
        Integer id = Integer.parseInt(idStr);
        ShProdDto data = shShopService.getById(id);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    @PostMapping("/getProds")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByUser(@RequestParam("userId") String userIdStr){
        Integer userId = Integer.parseInt(userIdStr.toString());
        List<ShProdDto> data = shShopService.getProdsByUser(userId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }

    @PostMapping("/myProds")
    public ResponseEntity<ApiResponse<List<ShProdDto>>> getProdsByUser(HttpSession session){
        Object userIdStr =  session.getAttribute("account");
        Integer userId = Integer.parseInt(userIdStr.toString());
        List<ShProdDto> data = shShopService.getProdsByUser(userId);
        return ResponseEntity.ok(ApiResponseFactory.success(data));
    }


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
                    .body(ApiResponseFactory.error(400, "尚未登入，請重新登入"));
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










}
