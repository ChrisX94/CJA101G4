package com.shakemate.ordermaster.controller;

import com.shakemate.ordermaster.config.EcpayLogisticsConfig;
import com.shakemate.ordermaster.config.EcpayPaymentConfig;
import com.shakemate.ordermaster.dto.ShOrderDto;
import com.shakemate.ordermaster.eum.PaymentStatus;
import com.shakemate.ordermaster.eum.ShippingStatus;
import com.shakemate.ordermaster.service.EcpayService;
import com.shakemate.ordermaster.service.ShOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@Slf4j
@RestController
@RequestMapping("/ecpay")
public class EcpayController {

    @Autowired
    EcpayService ecpayService;

    @Autowired
    ShOrderService orderService;

    @Autowired
    private EcpayPaymentConfig paymentConfig;

    @Autowired
    private EcpayLogisticsConfig logisticsConfig;


    @GetMapping("/logisticsMap")
    public String openCvsMap(@RequestParam String cvsType) {
        String merchantTradeNo = "Test" + System.currentTimeMillis();
        Map<String, String> param = new HashMap<>();
        param.put("MerchantID", logisticsConfig.getMerchantId());
        param.put("MerchantTradeNo", merchantTradeNo);
        param.put("LogisticsType", "CVS");
        param.put("LogisticsSubType", cvsType);
        param.put("IsCollection", "N");
        param.put("ServerReplyURL", logisticsConfig.getServerReplyUrl());
        param.put("ExtraData", "TestData");
        param.put("Device", "0");

        String checkMacValue = ecpayService.generateCheckMacValueForLogistics(param);
        param.put("CheckMacValue", checkMacValue);

        StringBuilder html = new StringBuilder();
        html.append("<html><body onload=\"document.forms[0].submit()\">")
                .append("<form method='POST' action='")
                .append(logisticsConfig.getMapUrl()).append("'>");

        param.forEach((k, v) -> html.append("<input type='hidden' name='")
                .append(k).append("' value='")
                .append(v).append("'/>"));

        html.append("</form>")
                .append("<p>正在開啟超商地圖...</p>")
                .append("</body></html>");

        return html.toString();
    }

    /*
     * ✔ 綠界物流選店完成，ServerReplyURL回傳資料
     * https://developers.ecpay.com.tw/?p=2856
     */
    @PostMapping("/cvsMapReturn")
    public String cvsMapReturn(@RequestParam Map<String, String> params) {
        System.out.println("===== 綠界超商回傳資料 =====");
        params.forEach((k, v) -> System.out.println(k + " = " + v));

        String storeId = params.getOrDefault("CVSStoreID", "");
        String storeName = params.getOrDefault("CVSStoreName", "");
        String storeAddress = params.getOrDefault("CVSAddress", "");
        String storeTelephone = params.getOrDefault("CVSTelephone", "");

        return "<html><body>" +
                "<script>" +
                "if (window.opener) {" +
                "   window.opener.postMessage({" +
                "       storeId: '" + storeId + "'," +
                "       storeName: '" + storeName + "'," +
                "       storeAddress: '" + storeAddress + "'," +
                "       storeTelephone: '" + storeTelephone + "'" +
                "   }, '*');" +
                "   window.close();" +
                "} else {" +
                "   alert('無法傳送門市資料，請手動關閉此視窗');" +
                "}" +
                "</script>" +
                "<h3>門市選擇完成，請關閉此視窗</h3>" +
                "</body></html>";
    }


    // 線上付款
    @PostMapping("/checkout")
    public String checkout(@RequestParam("orderId") Integer orderId,
                           @RequestParam("totalAmount") String totalAmount) {
        String orderDesc = "MatchMarketOrder";
        String merchantTradeNo = "SMP" + System.currentTimeMillis();
        String merchantTradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Map<String, String> form = new LinkedHashMap<>();
        form.put("ChoosePayment", "ALL");
        form.put("ClientBackURL", paymentConfig.getClientBackUrl());
        form.put("EncryptType", "1");
        form.put("ItemName", merchantTradeNo);
        form.put("NeedExtraPaidInfo", "N");
        form.put("MerchantID", paymentConfig.getMerchantId());
        form.put("MerchantTradeDate", merchantTradeDate);
        form.put("MerchantTradeNo", merchantTradeNo);
        form.put("PaymentType", "aio");
        form.put("ReturnURL", paymentConfig.getReturnUrl());
        form.put("TotalAmount", totalAmount);
        form.put("TradeDesc", orderDesc);
        form.put("CustomField1", orderId.toString());

        String checkMacValue = ecpayService.generateCheckMacValueForPayment(form);
        form.put("CheckMacValue", checkMacValue);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body onload=\"document.forms[0].submit()\">")
                .append("<form id=\"ecpayForm\" method=\"post\" action=\"")
                .append(paymentConfig.getApiUrl())
                .append("\">");

        form.forEach((k, v) -> sb.append("<input type='hidden' name='")
                .append(k).append("' value='")
                .append(v).append("'/>"));

        sb.append("</form>")
                .append("<h3>導向綠界付款頁面中...</h3>")
                .append("</body></html>");

        return sb.toString();
    }


    // 綠界物流 - 建立物流訂單（含代收/不代收）
    @PostMapping("/logisticsCvs")
    public ResponseEntity<String> createLogisticsOrder(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("totalAmount") Integer totalAmount,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("cvsType") String cvsType,
            @RequestParam("storeId") String storeId,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam("isCod") boolean isCod // ✅ true=貨到付款，false=線上付款
    ) {
        String merchantTradeNo = "SML" + System.currentTimeMillis();
        String tradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Map<String, String> param = new LinkedHashMap<>();
        param.put("MerchantID", logisticsConfig.getMerchantId());
        param.put("MerchantTradeNo", merchantTradeNo);
        param.put("MerchantTradeDate", tradeDate);
        param.put("LogisticsType", "CVS");
        param.put("LogisticsSubType", cvsType);
        param.put("GoodsAmount", String.valueOf(totalAmount));
        param.put("CollectionAmount", isCod ? String.valueOf(totalAmount) : "0");
        param.put("IsCollection", isCod ? "Y" : "N");
        param.put("GoodsName", "MatchMarketOrder");
        param.put("SenderName", "SMMM");
        param.put("SenderCellPhone", "0912345678");
        param.put("ReceiverName", receiverName);
        param.put("ReceiverCellPhone", receiverPhone);
        param.put("TradeDesc", orderId.toString());
        param.put("ReceiverStoreID", storeId);
        param.put("ReturnStoreID", storeId);
        param.put("ServerReplyURL", logisticsConfig.getCreatedReply());
        param.put("LogisticsC2CReplyURL", logisticsConfig.getC2cReply());
        param.put("Remark", remarks);

        String checkMacValue = ecpayService.generateCheckMacValueForLogistics(param);
        param.put("CheckMacValue", checkMacValue);

        // ✔ 組裝POST BODY
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            postData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        // ✔ 發送HTTP POST
        try {
            URL url = new URL(logisticsConfig.getCreateUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData.toString().getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("建立超商物流失敗：" + e.getMessage());
        }
    }


    @PostMapping("/logisticsPost")
    public ResponseEntity<String> createLogisticsPost(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("totalAmount") Integer totalAmount,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("ReceiverZipCode") String receiverZipCode,
            @RequestParam("ReceiverAddress") String receiverAddress,
            @RequestParam(value = "remarks", required = false) String remarks
    ) throws IOException {
        String merchantTradeNo = "MM" + orderId + "I" + System.currentTimeMillis();
        String tradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Map<String, String> param = new LinkedHashMap<>();
        param.put("MerchantID", logisticsConfig.getMerchantId());
        param.put("MerchantTradeNo", merchantTradeNo);
        param.put("MerchantTradeDate", tradeDate);
        param.put("LogisticsType", "HOME");
        param.put("LogisticsSubType", "POST");
        param.put("GoodsAmount", String.valueOf(totalAmount));
        param.put("GoodsName", "MatchMarketOrder");
        param.put("GoodsWeight", "8");
        param.put("SenderName", "SMMM");
        param.put("SenderCellPhone", "0912345678");
        param.put("SenderZipCode", "320006");
        param.put("SenderAddress", "桃園市中壢區復興路46號8樓");
        param.put("ReceiverName", receiverName);
        param.put("ReceiverCellPhone", receiverPhone);
        param.put("ReceiverZipCode", receiverZipCode);
        param.put("ReceiverAddress", receiverAddress);
        param.put("TradeDesc", orderId.toString());
        param.put("ServerReplyURL", logisticsConfig.getCreatedReply());
        param.put("LogisticsC2CReplyURL", logisticsConfig.getC2cReply());
        param.put("Remark", remarks);

        String checkMacValue = ecpayService.generateCheckMacValueForLogistics(param);
        param.put("CheckMacValue", checkMacValue);

        // 組成 POST Body
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            postData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        try {
            // 發送 POST 請求到綠界
            URL url = new URL(logisticsConfig.getCreateUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData.toString().getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("建立物流失敗：" + e.getMessage());
        }

    }


    // 金流付款回傳通知
    @PostMapping("/notify")
    public String receivePaymentNotify(@RequestParam Map<String, String> params) {
        log.info("綠界付款通知收到：{}", params);
        System.out.println("===== 綠界付款回傳資料 =====");
        params.forEach((key, value) -> System.out.println(key + " = " + value));

        // 取得 CheckMacValue
        String checkMacFromEcpay = params.get("CheckMacValue");

        //  用相同參數（扣除 CheckMacValue）重新計算 CheckMac
        String calculatedMac = ecpayService.generateCheckMacValueForPayment(params);


        if (!checkMacFromEcpay.equalsIgnoreCase(calculatedMac)) {
            log.error(" CheckMacValue 驗證失敗！收到：{}，計算：{}", checkMacFromEcpay, calculatedMac);
            return "0|Fail";
        }

        log.info("CheckMacValue 驗證成功！");


        // 3️⃣ 判斷付款是否成功
        String rtnCode = params.get("RtnCode");
        if ("1".equals(rtnCode)) {
            String merchantTradeNo = params.get("MerchantTradeNo");  // 你的訂單編號
            String tradeNo = params.get("TradeNo");          // 綠界交易編號
            String amount = params.get("TradeAmt");
            String paymentType = params.get("PaymentType");
            String orderId = params.get("CustomField1");
            String paymentDate = params.get("PaymentDate");
            String returnCode = params.get("RtnCode");
            String returnMsg = params.get("RtnMsg");

            log.info("✅ 付款成功！訂單：{}，金額：{}，付款時間：{}", orderId, amount, paymentDate);
            for (String key : params.keySet()) {
                System.out.println(key + " = " + params.get(key));
            }
            orderService.paymentStatus(Integer.parseInt(orderId), PaymentStatus.PAID);
        } else {
            log.warn("⚠️ 付款失敗！RtnCode = {}", rtnCode);
        }
        return "1|OK";
    }

    // 物流狀態回傳(綠界物流找不到模擬出貨)
    @PostMapping("/logisticsReply")
    public String logisticsReply(@RequestParam Map<String, String> params) {
        log.info("📦 綠界物流回傳：{}", params);
        String checkMac = params.get("CheckMacValue");
        String validateMac = ecpayService.generateCheckMacValueForLogistics(params);
        if (!checkMac.equalsIgnoreCase(validateMac)) {
            log.error("Logistics CheckMac 驗證失敗！");
            return "0|Fail";
        }
        String rtnCode = params.get("RtnCode");
        String orderId = params.get("MerchantTradeNo");
        for (String key : params.keySet()) {
            System.out.println(key + " = " + params.get(key));
        }
        if ("300".equals(rtnCode)) {
            log.info("✅ 超商取貨成功，訂單：{}", orderId);
        } else {
            log.warn("⚠️ 物流異常：{}", params.get("RtnMsg"));
        }
        return "1|OK";
    }

    // 物流狀態回傳(綠界物流找不到模擬出貨)
    @PostMapping("/ecpay/logisticsC2CReply")
    public String logisticsC2CReply(@RequestParam Map<String, String> params) {
        log.info("📦 [LogisticsC2CReply] 回傳資料：{}", params);
        String checkMac = params.get("CheckMacValue");
        String calculatedMac = ecpayService.generateCheckMacValueForLogistics(params);
        if (!checkMac.equalsIgnoreCase(calculatedMac)) {
            log.error(" CheckMac 驗證失敗！");
            return "0|Fail";
        }
        // 2️⃣ 取得重要參數
        String merchantTradeNo = params.get("MerchantTradeNo"); // 你的訂單編號
        String rtnCode = params.get("RtnCode");
        String rtnMsg = params.get("RtnMsg");
        for (String key : params.keySet()) {
            System.out.println(key + " = " + params.get(key));
        }
        log.info("訂單編號：{}，狀態碼：{}，訊息：{}", merchantTradeNo, rtnCode, rtnMsg);
        // 3️⃣ 根據狀態更新訂單
        switch (rtnCode) {
            case "2067":
                // ➜ 貨到超商
//                orderService.updateStatus(merchantTradeNo, "到店待取");
                System.out.println("2067 ➜ 貨到超商 ");
                break;
            case "3022":
                // ➜ 取貨完成
//                orderService.updateStatus(merchantTradeNo, "已完成");
                System.out.println("3022 ➜ 取貨完成");
                break;
            case "3025":
                // ➜ 退貨完成
//                orderService.updateStatus(merchantTradeNo, "退貨完成");
                System.out.println("3025 ➜ 退貨完成");
                break;
            default:
                log.warn("⚠️ 未處理的狀態：{}", rtnCode);
                break;
        }

        return "1|OK"; // 務必回傳，否則綠界會重送
    }


    @PostMapping("/mockLogistic")
    public String mockLogistic(@RequestParam Map<String, String> params) {
        String orderIdStr = params.get("orderId");
        String shipmentStatus = params.get("shipmentStatus"); // 0 processing, 1 shipped, 2 delivered
        String StatusDescription = params.get("statusDescription"); // description of the shipment status
        String isCollect = params.get("isCollect");  // 0 no, 1 yes, 2 refound, Only for pay on delivered
        Integer orderId = Integer.parseInt(orderIdStr);
        switch (shipmentStatus) {
            case "0":
                orderService.logisticStatus(orderId, ShippingStatus.PREPARING);
                break;
            case "1":
                orderService.logisticStatus(orderId, ShippingStatus.SHIPPED);
                break;
            case "2":
                orderService.logisticStatus(orderId, ShippingStatus.DELIVERED);
                break;
            case "3":
                orderService.logisticStatus(orderId, ShippingStatus.RETURN);
                break;
            case "4":
                orderService.logisticStatus(orderId, ShippingStatus.CANCELLED);
                break;
            default:
                break;
        }
        ShOrderDto data= orderService.getOrderById(orderId);
        if (isCollect != null && data.getOrderStatus() != PaymentStatus.UNPAID.getCode() ) {
            switch (isCollect) {
                case "0":
                    orderService.paymentStatus(orderId, PaymentStatus.UNPAID);
                    break;
                case "1":
                    orderService.paymentStatus(orderId, PaymentStatus.PAID);
                    break;
                case "2":
                    orderService.paymentStatus(orderId, PaymentStatus.REFUNDED);
                    break;
                case "3":
                    orderService.paymentStatus(orderId, PaymentStatus.CANCELLED);
                default:
                    break;
            }

            orderService.checkingStatus(orderId);
        }
        return "1|OK";
    }
}
