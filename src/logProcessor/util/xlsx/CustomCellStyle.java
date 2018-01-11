package logProcessor.util.xlsx;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CustomCellStyle {

  //单元格格式：货币（人民币￥）
  public static void currency(XSSFCellStyle cellStyle, XSSFWorkbook workbook) {
    cellStyle = workbook.createCellStyle();
    XSSFDataFormat xssfDataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(xssfDataFormat.getFormat("¥#,##0.00"));
  }

  //单元格格式：日期（yyyy-mm-dd hh:mm:ss）
  public static void dateAndTime(XSSFCellStyle cellStyle, XSSFWorkbook workbook) {
    cellStyle = workbook.createCellStyle();
    XSSFDataFormat xssfDataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(xssfDataFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
  }

  //单元格格式：日期（yyyy-mm-dd hh:mm:ss）
  public static void date(XSSFCellStyle cellStyle, XSSFWorkbook workbook) {
    cellStyle = workbook.createCellStyle();
    XSSFDataFormat xssfDataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(xssfDataFormat.getFormat("yyyy-mm-dd"));
  }

  //单元格格式：水平居中
  public static void verticalCenter(XSSFCellStyle cellStyle) {
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    cellStyle.setAlignment(HorizontalAlignment.CENTER);
  }

  //单元格格式：水平右对齐
  public static void verticalRight(XSSFCellStyle cellStyle) {
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
  }

  //单元格格式：全边框
  public static void outlineBorder(XSSFCellStyle cellStyle) {
    cellStyle.setBorderLeft(BorderStyle.THIN);
    cellStyle.setBorderRight(BorderStyle.THIN);
    cellStyle.setBorderTop(BorderStyle.THIN);
    cellStyle.setBorderBottom(BorderStyle.THIN);
  }
}
