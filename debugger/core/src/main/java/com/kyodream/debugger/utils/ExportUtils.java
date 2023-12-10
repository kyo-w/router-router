package com.kyodream.debugger.utils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExportUtils {

//    public static void exportAllData(HashMap<String, DefaultHandlerFramework> data, HttpServletResponse response) {
//        HSSFWorkbook wb = new HSSFWorkbook();
//        Iterator<Map.Entry<String, DefaultHandlerFramework>> iterator = data.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, DefaultHandlerFramework> next = iterator.next();
//            HashMap<String, String> dataWrapper = next.getValue().getDataWrapper();
//            if(dataWrapper.size() == 0){
//                continue;
//            }
//
//            String key = next.getKey();
//            HSSFSheet sheet = wb.createSheet(key);
//            sheet.createFreezePane(0, 1);
//            HSSFRow row = sheet.createRow(0);
//            row.createCell(0).setCellValue("api");
//            row.createCell(1).setCellValue("className");
//
//            Iterator<Map.Entry<String, String>> iterator1 = dataWrapper.entrySet().iterator();
//            Integer index = 1;
//            while (iterator1.hasNext()){
//                Map.Entry<String, String> next1 = iterator1.next();
//                String key1 = next1.getKey();
//                String value = next1.getValue();
//                HSSFRow row1 = sheet.createRow(index);
//                row1.createCell(0).setCellValue(key1);
//                row1.createCell(1).setCellValue(value);
//                index++;
//            }
//        }
//
//        try {
//            response.setHeader("content-disposition","attachment;filename="+ URLEncoder.encode("output.xls","UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            wb.write(response.getOutputStream());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
