package utils;

import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExcelUtil {
    public static void saveToXls(String filepath, String[] title, String[][] data) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);
        for(int i=0; i< title.length; i++){
            row.createCell(i).setCellValue(title[i]);
        }

        for(int i=1; i < data.length + 1;i++){
            HSSFRow row1 = sheet.createRow(i);
            for(int j=0; j < data[i -1].length; j++){
                row1.createCell(j).setCellValue(data[i -1][j]);
            }
        }

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filepath);
            wb.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
