package com.shakemate.ordermaster.controller;

import com.shakemate.ordermaster.config.EcpayLogisticsConfig;
import com.shakemate.ordermaster.config.EcpayPaymentConfig;
import com.shakemate.ordermaster.service.EcpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
@RestController
@RequestMapping("/ecpay")
public class EcpayController {

    @Autowired
    EcpayService ecpayService;

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

    /**
     * ✔ 綠界物流選店完成，ServerReplyURL回傳資料
     * ✔ 正確使用 postMessage 傳資料回主頁
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

    @GetMapping("/checkout")
    public String checkout(@RequestParam("orderId") Integer orderId,
                           @RequestParam("totalAmount") String totalAmount) {
        String orderDesc = "MatchMarketOrder";
        String merchantTradeNo = "MM" + System.currentTimeMillis();
        String merchantTradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Map<String, String> form = new LinkedHashMap<>();
        form.put("ChoosePayment", "ALL");
        form.put("ClientBackURL", paymentConfig.getClientBackUrl());
        form.put("EncryptType", "1");
        form.put("ItemName", "MatchMarketOrderId" + orderId);
        form.put("NeedExtraPaidInfo", "N");
        form.put("MerchantID", paymentConfig.getMerchantId());
        form.put("MerchantTradeDate", merchantTradeDate);
        form.put("MerchantTradeNo", merchantTradeNo);
        form.put("PaymentType", "aio");
        form.put("ReturnURL", paymentConfig.getReturnUrl());
        form.put("TotalAmount", totalAmount);
        form.put("TradeDesc",orderDesc);

        String checkMacValue = ecpayService.generateCheckMacValueForPayment(form);
        form.put("CheckMacValue", checkMacValue);



        StringBuilder sb = new StringBuilder();
        sb.append("<html><body onload=\"document.forms[0].submit()\">")
                .append("<form id=\"ecpayForm\" method=\"post\" action=\"")
                .append(paymentConfig.getApiUrl())
                .append("\">");

        form.forEach((k, v) -> sb.append("<input type='hidden' name='")
                .append(k).append("' value='")
                .append(v).append("'/>"));  // 🚩 不要做 encode，直接填 value

        sb.append("</form>")
                .append("<h3>導向綠界付款頁面中...</h3>")
                .append("</body></html>");

        return sb.toString();
    }



}
