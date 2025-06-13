package com.shakemate.shshop.controller;


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
    public List<ShProdDto> getAllShProds(){
        return shShopService.getAll();
    }

    @GetMapping("/allTypes")
    public List<ShProdTypeDto> getAllType(){
        return shShopService.getAllType();
    }

    @GetMapping("/getProd")
    public ShProdDto getProdsById(@RequestParam("id") String idStr){
        Integer id = Integer.parseInt(idStr);
        return shShopService.getById(id);
    }

    @PostMapping("/getProd")
    public ShProdDto getProdsByIdPost(@RequestParam("id") String idStr){
        Integer id = Integer.parseInt(idStr);
        return shShopService.getById(id);
    }

    @PostMapping("/getProds")
    public List<ShProdDto> getProdsByUser(@RequestParam("userId") String userIdStr){
        Integer userId = Integer.parseInt(userIdStr);
        return shShopService.getProdsByUser(userId);
    }

    @PostMapping("/addNewProd")
    public ShProdDto addNewProd(@RequestParam("prodName") String prodName,
                                @RequestParam("prodBrand") String prodBrand,
                                @RequestParam("prodTypeId") String prodTypeIdStr,
                                @RequestParam("prodPrice") String prodPriceStr,
                                @RequestParam("prodCount") String prodCountStr,
                                @RequestParam("prodContent") String prodContent,
                                @RequestParam("prodStatusDesc") String prodStatusDesc,
                                @RequestParam("prodImage") MultipartFile[] parts,
                                HttpSession session
                                ) throws IOException {
        ShProd shProd = new ShProd();
        List<String> shProdPicUrls = new ArrayList<String>();
        for (MultipartFile p : parts) {
            if ("prodImage".equals(p.getName()) && p.getSize() > 0) {
                String shProdPicUrl = postImageUploader.uploadImageToImgbb(p);
                shProdPicUrls.add(shProdPicUrl);
            }
        }
        String[] picUrlArray = shProdPicUrls.toArray(new String[0]);

//        Object userIdObj = session.getAttribute("userId");
//        Integer userId=null;
//        if(userIdObj == null){
//            return ResponseEntity.status(401).body("尚未登入");
//        }else{
//            userId = (Integer)userIdObj;
//        }

        Integer userId =1; // test only
        Users user = new Users();
        user.setUserId(userId);
        shProd.setUser(user);

        Integer prodPrice = Integer.parseInt(prodPriceStr);
        Integer prodCount = Integer.parseInt(prodCountStr);
        Integer shProdTypeId = Integer.parseInt(prodTypeIdStr);
        shProd.setProdName(prodName);
        shProd.setProdBrand(prodBrand);
        shProd.setProdPrice(prodPrice);
        shProd.setProdCount(prodCount);
        ShProdType type = new ShProdType();
        type.setProdTypeId(shProdTypeId);
        shProd.setProdType(type);
        shProd.setProdContent(prodContent);
        shProd.setProdStatusDesc(prodStatusDesc);
        List<ShProdPic> shProdPic = shProdPic(picUrlArray, shProd);
        shProd.setProdPics(shProdPic);
        // default
        shProd.setProdViews(0);
        shProd.setProdStatus((byte)0);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        shProd.setProdRegTime(now);
        shProd.setUpdatedTime(now);
        return shShopService.addShProd(shProd);

    }






    private List<ShProdPic> shProdPic(String[] shProdPicUrl, ShProd shProd) {
        List<ShProdPic> pics = shProd.getProdPics();
        if (pics == null) {
            // 新增時：原 list 為 null，初始化
            pics = new ArrayList<>();
            shProd.setProdPics(pics);
        } else {
            // 更新時：清除舊圖（會觸發 Hibernate orphanRemoval 自動刪除）
            pics.clear();
        }
        // 新增新圖（無論是新增或更新）
        if (shProdPicUrl != null && shProdPicUrl.length > 0) {
            for (String url : shProdPicUrl) {
                ShProdPic newPic = new ShProdPic();
                newPic.setShProd(shProd);
                newPic.setProdPic(url);
                pics.add(newPic);
            }
        }
        return pics;
    }



}
//import com.google.gson.Gson;
//
//
//import com.shakemate.util.PostImageUploader;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//@MultipartConfig
//@WebServlet("/newOne")
//public class SHShopController extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//
//    private ShShopService service;
//    private PostImageUploader postImageUploader;
//
//    public void init() throws ServletException {
//        service = new ShShopService();
//        postImageUploader = new PostImageUploader();
//    }
//
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        req.setCharacterEncoding("UTF-8");
//        res.setHeader("Access-Control-Allow-Origin", "*");
//        res.setHeader("Access-Control-Allow-Methods", "GET, POST");
//        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
//
//        PrintWriter out = res.getWriter();
//        res.setContentType("application/json; charset=utf-8");
//
//        String reqURI = req.getRequestURI();
//        String param = reqURI.substring(reqURI.lastIndexOf("/") + 1);
//        System.out.println(reqURI);
//        System.out.println(param);
//        String action = req.getParameter("action");  // query string 中的 action
//
//        System.out.println("action = " + action);
//
//        if (action == null) {
//            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter");
//            return;
//        }
//
//        String jsonStr = "";
//        switch (action) {
//            case "getAll":
//                jsonStr = getAllSHProd();
//                break;
//            case "getProd":
//                jsonStr = getOnrByPK(req);
//                break;
//            case "allTypes":
//                jsonStr = service.getAllType();
//                break;
//            default:
//                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
//                return;
//        }
//
//        out.write(jsonStr);
//        out.flush();
//        out.close();
//
//    }
//
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        res.setHeader("Access-Control-Allow-Origin", "*");
//        res.setHeader("Access-Control-Allow-Methods", "GET, POST");
//        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
//
//
//        PrintWriter out = res.getWriter();
//        res.setContentType("application/json; charset=utf-8");
//        String action = req.getParameter("action");  // query string 中的 action
//        System.out.println("action = " + action);
//        if (action == null) {
//            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing action parameter");
//        }
//        String jsonStr = "";
//        switch (action) {
//            case "createProd":
//                jsonStr = createNewProd(req, res);
////                String url = "/shshop/product_page.html";
////                RequestDispatcher successMsg = req.getRequestDispatcher(url);
//                break;
//            case "getUserProdList":
//                jsonStr = getUserProdList(req);
//                break;
//            case "getProd":
//                jsonStr = getOnrByPK(req);
//                break;
//            case "updateProd":
//                jsonStr = updateOneByPK(req);
//                break;
//            default:
//                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
//                return;
//        }
//        if (jsonStr == null || jsonStr.isEmpty()) {
//            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
//            return;
//        }
//
//        out.write(jsonStr);
//        out.flush();
//        out.close();
//
//    }
//
//    String getUserProdList(HttpServletRequest req) {
//
//        //        HttpSession session = req.getSession();
////        Integer id = (Integer) session.getAttribute("id");
////        if (id == null) {
////            return "please login";
////        }
//        Integer userId = 1; // 測試用
//        String jsonStr = service.getProdsByUser(userId);
//        return jsonStr;
//    }
//
//    String updateOneByPK(HttpServletRequest req) throws ServletException, IOException {
//        Gson gson = new Gson();
//        Map<String, String> errors = new HashMap<>();
//        Map<String, Object> results = new HashMap<>();
////        HttpSession session = req.getSession();
////        Integer id = (Integer) session.getAttribute("id");
////        if (id == null) {
////            return "please login";
////        }
//
//        Integer userId = 1; // 測試用
//        String prodIdStr = req.getParameter("prodId");
//        Integer prodId = Integer.parseInt(prodIdStr);
//        String prodName = req.getParameter("prodName");
//        String prodBrand = req.getParameter("prodBrand");
//        String prodContent = req.getParameter("prodContent");
//        String prodStatusDesc = req.getParameter("prodStatusDesc");
//        errors = checkProdInfo(prodName, prodBrand, prodContent, prodStatusDesc, errors);
//        String prodPriceStr = req.getParameter("prodPrice");
//        String prodCountStr = req.getParameter("prodCount");
//        String prodTypeIdStr= req.getParameter("prodTypeId");
//        Map<String, Object>  returnInfo = checkNum(prodPriceStr, prodCountStr,prodTypeIdStr, errors);
//        errors = (Map) returnInfo.get("errors");
//
//        Integer prodPrice = (Integer) returnInfo.get("prodPrice");
//        Integer prodCount = (Integer) returnInfo.get("prodCount");
//        Integer shProdTypeId = (Integer) returnInfo.get("prodTypeId");
//        List<String> shProdPicUrls = new ArrayList<String>();
//        for (Part p : req.getParts()) { // 要用Parts 接參數，然後跑回圈
//            // prodImage 是前端接收圖片的參數, 取得每個Part 中name == prodImage, size > 0
//            if ("prodImage".equals(p.getName()) && p.getSize() > 0) {
//                String shProdPicUrl = postImageUploader.uploadImageToImgbb(p);
//                shProdPicUrls.add(shProdPicUrl);
//            }
//        }
//        String[] picUrlArray = shProdPicUrls.toArray(new String[0]);
//
//        String prodStatusStr = req.getParameter("prodStatus");
//
//        byte prodStatus = (byte) Integer.parseInt(prodStatusStr);
//
//        if(!errors.isEmpty()) {
//            results.put("status", "error");
//            results.put("errors", errors);
//            return gson.toJson(results);
//        }
//
//        String jsonStr = service. updateProd(prodId, prodName, shProdTypeId, prodContent, prodStatusDesc,prodPrice, prodCount, prodBrand,  prodStatus, picUrlArray);
//        results.put("status", "success");
//        results.put("message", "商品新增成功");
//        results.put("data", gson.fromJson(jsonStr, Map.class)); // 或保留 jsonStr 字串也可以
//        return gson.toJson(results);
//
//    }
//
//
//
//    String createNewProd(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//        Gson gson = new Gson();
//        Map<String, String> errors = new HashMap<>();
//        Map<String, Object> results = new HashMap<>();
//        //        HttpSession session = req.getSession();
////        Integer id = (Integer) session.getAttribute("id");
////        if (id == null) {
////            return "please login";
////        }
//
//        Integer userId = 1; // 測試用
//        String prodName = req.getParameter("prodName");
//        String prodBrand = req.getParameter("prodBrand");
//        String prodContent = req.getParameter("prodContent");
//        String prodStatusDesc = req.getParameter("prodStatusDesc");
//        errors = checkProdInfo(prodName, prodBrand, prodContent, prodStatusDesc, errors);
//
//        String prodPriceStr = req.getParameter("prodPrice");
//        String prodCountStr = req.getParameter("prodCount");
//        String prodTypeIdStr= req.getParameter("prodTypeId");
//        Map<String, Object>  returnInfo = checkNum(prodPriceStr, prodCountStr,prodTypeIdStr, errors);
//        errors = (Map) returnInfo.get("errors");
//
//        Integer prodPrice = (Integer) returnInfo.get("prodPrice");
//        Integer prodCount = (Integer) returnInfo.get("prodCount");
//        Integer shProdTypeId = (Integer) returnInfo.get("prodTypeId");
//        List<String> shProdPicUrls = new ArrayList<String>();
//        for (Part p : req.getParts()) { // 要用Parts 接參數，然後跑回圈
//            // prodImage 是前端接收圖片的參數, 取得每個Part 中name == prodImage, size > 0
//            if ("prodImage".equals(p.getName()) && p.getSize() > 0) {
//                String shProdPicUrl = postImageUploader.uploadImageToImgbb(p);
//                shProdPicUrls.add(shProdPicUrl);
//            }
//        }
//        String[] picUrlArray = shProdPicUrls.toArray(new String[0]);
//
//
//        byte prodStatus = 0; // 預設待審核
//
//
//        if(!errors.isEmpty()) {
//            results.put("status", "error");
//            results.put("errors", errors);
//            return gson.toJson(results);
//        }
//        String jsonStr = service.createNewProd(userId, prodName, shProdTypeId, prodContent,
//                prodStatusDesc, prodPrice, prodBrand, prodCount, prodStatus, picUrlArray);
//
//        results.put("status", "success");
//        results.put("message", "商品新增成功");
//        results.put("data", gson.fromJson(jsonStr, Map.class)); // 或保留 jsonStr 字串也可以
//        return gson.toJson(results);
//
//    }
//
//    String getOnrByPK(HttpServletRequest req) {
//        String ShProdIdStr = req.getParameter("id");
//        Integer ShProdId = Integer.parseInt(ShProdIdStr);
//        System.out.println("ShProdId = " + ShProdId);
//        String jsonStr = "";
//        jsonStr = service.getOneById(ShProdId);
//        System.out.println(jsonStr);
//        return jsonStr;
//    }
//
//    String getAllSHProd() {
//        String jsonStr = service.getAllJson();
//        System.out.println(jsonStr);
//        return jsonStr;
//    }
//
//    Map<String, String> checkProdInfo(String prodName, String prodBrand, String prodContent, String prodStatusDesc, Map<String, String> errors) {
//        String prodNameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{1,30}$"; // regx Chinese, number and _ , length is between 1-30
//        if (prodName == null || (prodName.trim()).isEmpty()) { // check null or blank
//            errors.put("prodName", "商品名稱不能空白");
//        } else if (!prodName.trim().matches(prodNameReg)) { // check format
//            errors.put("prodName", "商品名稱只能是中、英文和_，長度介於1到30之間");
//        }
//        if (prodBrand == null || prodBrand.isEmpty()) {
//            errors.put("prodBrand", "商品品牌不能空白");
//        }
//        if (prodContent == null || prodContent.isEmpty()) {
//            errors.put("prodContent", "商品內容不能空白");
//        }
//        if (prodStatusDesc == null || prodStatusDesc.isEmpty()) {
//            errors.put("prodStatusDesc", "商品敘述不能空白");
//        }
//        return errors;
//    }
//
//
//    Map<String, Object>  checkNum(String prodPriceStr, String prodCountStr, String prodTypeIdStr, Map<String, String> errors) {
//        Map<String, Object>  returnInfo = new HashMap<>();
//
//        if (prodPriceStr == null || (prodPriceStr).isEmpty()) {
//            errors.put("prodPrice", "商品價錢不能為空白");
//        }
//        Integer prodPrice = null;
//        try {
//            prodPrice = Integer.valueOf(prodPriceStr);
//            if (prodPrice <= 0) {
//                errors.put("prodPrice", "商品價格不能小於0");
//            }
//        } catch (NullPointerException e) {
//            errors.put("prodPrice", "必須是整數");
//
//        } catch (NumberFormatException e) {
//            errors.put("prodPrice", "必須是整數");
//        }
//        /////////////////
//        if (prodCountStr == null || (prodCountStr).isEmpty()) {
//            errors.put("prodCount", "商品數量不能為空白");
//        }
//        Integer prodCount = null;
//        try {
//            prodCount = Integer.valueOf(prodCountStr);
//            if (prodCount <= 0) {
//                errors.put("prodCount", "商品數量不能小於0");
//            }
//        } catch (NullPointerException e) {
//            errors.put("prodCount", "必須是整數");
//        } catch (NumberFormatException e) {
//            errors.put("prodCount", "商品數量不能為空白");
//        }
//        /// //////////////////////////////////
//        if (prodTypeIdStr == null || (prodTypeIdStr).isEmpty()) {
//            errors.put("prodTypeId", "請選擇商品類別");
//        }
//        Integer shProdTypeId = null;
//        try {
//            shProdTypeId = Integer.valueOf(prodTypeIdStr);
//        } catch (NullPointerException e) {
//            errors.put("prodTypeId", "必須是整數");
//        } catch (NumberFormatException e) {
//            errors.put("prodTypeId", "要選擇商品類型");
//        }
//
//
//        returnInfo.put("errors", errors);
//        returnInfo.put("prodPrice", prodPrice);
//        returnInfo.put("prodCount", prodCount);
//        returnInfo.put("prodTypeId", shProdTypeId);
//        return returnInfo;
//    }
//
//
//    @Override
//    public void destroy() {
//        super.destroy();
//    }
//}
