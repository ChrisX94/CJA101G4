package com.shakemate.shshop.util;

import com.shakemate.shshop.dto.ProdAuditResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class ExcelHandler {

    public byte[] generateAuditExcel(Map<String, List<ProdAuditResult>> data) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFCellStyle headerStyle = createHeaderCellStyle(wb);
            XSSFCellStyle bodyStyle = createBodyCellStyle(wb);
            String[] headers = {"商品ID", "商品名稱", "審核結果", "不通過原因"};
            int sheetIndex = 1;
            for (Map.Entry<String, List<ProdAuditResult>> entry : data.entrySet()) {
                String timeKey = entry.getKey();
                List<ProdAuditResult> dataList = entry.getValue();
                String sheetName = timeKey.replaceAll("[\\\\/:*?\\[\\]]", "_");
                sheetIndex++;
                XSSFSheet sheet = wb.createSheet(sheetName);
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
                for (ProdAuditResult item : dataList) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(item.getProdId() != null ? item.getProdId() : 0);
                    row.createCell(1).setCellValue(item.getProdName() != null ? item.getProdName() : "");
                    row.createCell(2).setCellValue(item.getStatus() != null ?
                            (item.getStatus().equals("approve") ? "通過" : "不通過") : "");
                    row.createCell(3).setCellValue(item.getReason() != null ? item.getReason() : "");

                    for (int i = 0; i < headers.length; i++) {
                        row.getCell(i).setCellStyle(bodyStyle);
                    }
                }
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                sheet.createFreezePane(0, 1);
                sheet.setColumnWidth(0, 10 * 256);  // 商品ID
                sheet.setColumnWidth(1, 30 * 256);  // 商品名稱
                sheet.setColumnWidth(2, 15 * 256);  // 審核結果
                sheet.setColumnWidth(3, 50 * 256);  // 不通過原因
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    private XSSFCellStyle createHeaderCellStyle(XSSFWorkbook workbook) {
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFCellStyle createBodyCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public String generateFileName() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "AI_Audit_" + timestamp + ".xlsx";
    }
}
