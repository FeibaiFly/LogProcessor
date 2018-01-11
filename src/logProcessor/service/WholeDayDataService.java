package logProcessor.service;

import static logProcessor.MainEntrance.configuration;
import logProcessor.bo.DownloadBo;
import logProcessor.bo.LogBo;
import logProcessor.util.SharedFoldersUtil;
import logProcessor.util.xlsx.CustomCellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WholeDayDataService {
  //Map<文件名,字体名>
  private static Map<String, String> fontNameMap = new HashMap<>();

  private static Map<String, DownloadBo> ttfDownloadStatistics = new HashMap<>();
  private static Map<String, DownloadBo> zipDownloadStatistics = new HashMap<>();

  //Map<字体名, Map<时间段, 下载量>>
  private static Map<String, Map<Integer, Integer>> timeSlotDownloadStatistics = new HashMap<>();

  void analyseLog(Map<Integer, List<LogBo>> map) {
    Connection connection = null;

    try {
      connection = DriverManager.getConnection(configuration.getCmsDatabaseUrl(), configuration.getDatabaseUserName(), configuration.getDatabaseUserPassword());
      ResultSet resultSet;
      for (Integer timeSlot : map.keySet()) {
        List<LogBo> list = map.get(timeSlot);
        for (LogBo bo : list) {
          String fileName = bo.getDownloadFile();
          String[] fragment = fileName.split("\\.");
          String format = fragment[fragment.length - 1];
          fileName = fileName.replace(format, "");
          String fontName = fontNameMap.get(fileName);

          //若字体名map中不包含改字体名，在数据库中查询并存入map
          if (fontName == null) {
            if ("ttf".equals(format)) {
              Statement statement = connection.createStatement();
              resultSet = statement.executeQuery(ttfSql(fileName));
            } else {
              Statement statement = connection.createStatement();
              resultSet = statement.executeQuery(zipSql(fileName));
            }
            resultSet.next();
            fontName = resultSet.getString("FONT_NAME");
            fontNameMap.put(fileName, fontName);
            Map<Integer, Integer> subMap = new HashMap<>();
            timeSlotDownloadStatistics.put(fontName, subMap);
          }

          //时间段下载数据统计
          if (timeSlotDownloadStatistics.get(fontName).get(timeSlot) == null) {
            timeSlotDownloadStatistics.get(fontName).put(timeSlot, 1);
          } else {
            timeSlotDownloadStatistics.get(fontName).put(timeSlot, timeSlotDownloadStatistics.get(fontName).get(timeSlot) + 1);
          }

          if ("ttf".equals(format)) {
            if (ttfDownloadStatistics.get(fontName) == null) {
              ttfDownloadStatistics.put(fontName, new DownloadBo());
            }
            DownloadBo downloadBo = ttfDownloadStatistics.get(fontName);
            downloadBo.totalIncrement();
            statistics(downloadBo, bo.getUserAgent());
          } else {
            if (zipDownloadStatistics.get(fontName) == null) {
              zipDownloadStatistics.put(fontName, new DownloadBo());
            }
            DownloadBo downloadBo = zipDownloadStatistics.get(fontName);
            downloadBo.totalIncrement();
            statistics(downloadBo, bo.getUserAgent());
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  void writeStatistic(Date date) {
    XSSFWorkbook workbook = new XSSFWorkbook();

    downloadStatisticByFont(workbook);

    downloadStatisticByTimeSlot(workbook);

    SharedFoldersUtil.uploadFile(workbook, date);

    try {
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static String ttfSql(String filename) {
    return "SELECT FONT_NAME FROM fs_font_pool WHERE TTF_URL LIKE '%" + filename + "%'";
  }

  private static String zipSql(String filename) {
    return "SELECT FONT_NAME FROM fs_font_pool WHERE ZIP_URL LIKE '%" + filename + "%'";
  }

  private static void statistics(DownloadBo bo, String userAgent) {
    if (userAgent.contains("okhttp/2.7.5")) {
      bo.sugarOrSoapIncrement();
    } else if (userAgent.contains("QEZB") && userAgent.contains("Darwin")) {
      bo.QEZBIosIncrement();
    } else if (userAgent.contains("Ubuntu") ||
            (userAgent.contains("Dalvik") && userAgent.contains("Android"))) {
      bo.otherUserMobileIncrement();
    } else {
      bo.pcOrOthersIncrement();
    }
  }

  private static void downloadStatisticByTimeSlot(XSSFWorkbook workbook) {
    XSSFSheet sheet = workbook.createSheet("字体下载情况（按时间段）");
    //列宽度（1像素=32）
    sheet.setColumnWidth(1, 160 * 32);
    for (int i = 0; i < 24; i++) {
      sheet.setColumnWidth(2 + i, 75 * 32);
    }
    //表头设置
    XSSFRow row = sheet.createRow(1);
    XSSFCellStyle cellStyle = workbook.createCellStyle();
    CustomCellStyle.verticalCenter(cellStyle);
    CustomCellStyle.outlineBorder(cellStyle);
    XSSFCellStyle numberStyle = workbook.createCellStyle();
    CustomCellStyle.outlineBorder(numberStyle);
    XSSFCell cell;
    cell = row.createCell(1);
    cell.setCellValue("字体名称");
    for (int i = 2; i < 26; i++) {
      row.createCell(i).setCellStyle(cellStyle);
    }
    cell = row.createCell(2);
    cell.setCellValue("时间");
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 25));
    cell.setCellStyle(cellStyle);
    row = sheet.createRow(2);
    row.createCell(1).setCellStyle(cellStyle);
    for (int i = 0; i < 24; i++) {
      cell = row.createCell(i + 2);
      cell.setCellStyle(cellStyle);
      cell.setCellValue(i + "时" + "-" + (i + 1) + "时");
    }
    sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
    row = sheet.getRow(1);
    cell = row.getCell(1);
    cell.setCellStyle(cellStyle);
    int rowNumber = 3;
    for (String fontName : timeSlotDownloadStatistics.keySet()) {
      row = sheet.createRow(rowNumber);
      cell = row.createCell(1);
      cell.setCellValue(fontName);
      cell.setCellStyle(cellStyle);
      for (int i = 0; i < 24; i++) {
        cell = row.createCell(i + 2);
        cell.setCellValue(timeSlotDownloadStatistics.get(fontName).get(i) == null ? 0 : timeSlotDownloadStatistics.get(fontName).get(i));
        cell.setCellStyle(numberStyle);
      }
      rowNumber++;
    }
    row = sheet.createRow(rowNumber);
    cell = row.createCell(1);
    cell.setCellStyle(cellStyle);
    cell.setCellValue("总计");

    for (int i = 0; i < 24; i++) {
      cell = row.createCell(i + 2);
      cell.setCellStyle(numberStyle);
      char column = (char) (67 + i);
      cell.setCellFormula("SUM(" + column + "4:" + column + rowNumber + ")");
    }

    Drawing drawing = sheet.createDrawingPatriarch();
    ClientAnchor clientAnchor = drawing.createAnchor(0, 0, 0, 0, 2, rowNumber + 2, 18, rowNumber + 22);
    Chart chart = drawing.createChart(clientAnchor);

    //ChartLegend legend = chart.getOrCreateLegend();
    //legend.setPosition(LegendPosition.TOP_RIGHT);


    LineChartData data = chart.getChartDataFactory().createLineChartData();

    ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
    ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
    leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

    ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(2, 2, 2, 25));
    ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(rowNumber, rowNumber, 2, 25));

    data.addSeries(xs, ys1);

    chart.plot(data, bottomAxis, leftAxis);

  }

  private static void downloadStatisticByFont(XSSFWorkbook workbook) {
    XSSFSheet sheet = workbook.createSheet("字体下载情况（按字体）");

    //列宽度（1像素=32）
    sheet.setColumnWidth(1, 160 * 32);
    sheet.setColumnWidth(2, 80 * 32);
    sheet.setColumnWidth(3, 120 * 32);
    sheet.setColumnWidth(4, 120 * 32);
    sheet.setColumnWidth(5, 120 * 32);
    sheet.setColumnWidth(6, 120 * 32);
    sheet.setColumnWidth(7, 80 * 32);
    sheet.setColumnWidth(8, 120 * 32);
    sheet.setColumnWidth(9, 120 * 32);
    sheet.setColumnWidth(10, 120 * 32);
    sheet.setColumnWidth(11, 120 * 32);

    //表头设置
    XSSFRow row = sheet.createRow(1);
    XSSFCellStyle cellStyle = workbook.createCellStyle();
    CustomCellStyle.verticalCenter(cellStyle);
    CustomCellStyle.outlineBorder(cellStyle);
    XSSFCell cell;
    for (int i = 1; i < 12; i++) {
      cell = row.createCell(i);
      cell.setCellStyle(cellStyle);
    }
    cell = row.getCell(1);
    cell.setCellValue("字体名称");
    cell = row.getCell(2);
    cell.setCellValue("ZIP文件下载次数");
    cell = row.getCell(7);
    cell.setCellValue("TTF文件下载次数");
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 6));
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 11));
    row = sheet.createRow(2);
    for (int i = 1; i < 12; i++) {
      cell = row.createCell(i);
      cell.setCellStyle(cellStyle);
    }
    sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
    row.getCell(2).setCellValue("总计");
    row.getCell(3).setCellValue("SUGAR/SOAP");
    row.getCell(4).setCellValue("企鹅直播IOS");
    row.getCell(5).setCellValue("其他手机下载");
    row.getCell(6).setCellValue("PC及其他");
    row.getCell(7).setCellValue("总计");
    row.getCell(8).setCellValue("SUGAR/SOAP");
    row.getCell(9).setCellValue("企鹅直播IOS");
    row.getCell(10).setCellValue("其他手机下载");
    row.getCell(11).setCellValue("PC及其他");

    int rowNumber = 3;
    XSSFCellStyle valueStyle = workbook.createCellStyle();
    CustomCellStyle.outlineBorder(valueStyle);
    for (String fontName : fontNameMap.values()) {
      row = sheet.createRow(rowNumber);
      cell = row.createCell(1);
      cell.setCellValue(fontName);
      cell.setCellStyle(cellStyle);
      DownloadBo zipBo = zipDownloadStatistics.get(fontName);
      DownloadBo ttfBo = ttfDownloadStatistics.get(fontName);

      if (zipBo == null) {
        zipBo = new DownloadBo();
      }
      if (ttfBo == null) {
        ttfBo = new DownloadBo();
      }

      cell = row.createCell(2);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(zipBo.getTotal());
      cell = row.createCell(3);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(zipBo.getSugarOrSoap());
      cell = row.createCell(4);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(zipBo.getQEZBIos());
      cell = row.createCell(5);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(zipBo.getOtherUserMobile());
      cell = row.createCell(6);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(zipBo.getPcOrOthers());
      cell = row.createCell(7);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(ttfBo.getTotal());
      cell = row.createCell(8);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(ttfBo.getSugarOrSoap());
      cell = row.createCell(9);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(ttfBo.getQEZBIos());
      cell = row.createCell(10);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(ttfBo.getOtherUserMobile());
      cell = row.createCell(11);
      cell.setCellStyle(valueStyle);
      cell.setCellValue(ttfBo.getPcOrOthers());
      rowNumber++;
    }
    //插入总下载量
    row = sheet.createRow(rowNumber);
    for (int i = 1; i < 12; i++) {
      cell = row.createCell(i);
      cell.setCellStyle(valueStyle);
    }
    cell = row.getCell(1);
    cell.setCellValue("下载量");
    cell.setCellStyle(cellStyle);
    //ZIP下载总量
    cell = row.getCell(2);
    cell.setCellType(CellType.FORMULA);
    cell.setCellFormula("SUM(C3:C" + rowNumber + ")");
    sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 2, 6));

    //TTF下载总量
    cell = row.getCell(7);
    cell.setCellType(CellType.FORMULA);
    cell.setCellFormula("SUM(L3:L" + rowNumber + ")");
    sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 7, 11));

    //文件下载总量
    row = sheet.createRow(rowNumber + 1);
    for (int i = 1; i < 12; i++) {
      cell = row.createCell(i);
      cell.setCellStyle(valueStyle);
    }
    cell = row.getCell(1);
    cell.setCellValue("总下载量");
    cell.setCellStyle(cellStyle);
    cell = row.getCell(2);
    cell.setCellType(CellType.FORMULA);
    cell.setCellFormula("SUM(C" + (rowNumber + 1) + ":L" + (rowNumber + 1) + ")");

    //合并单元格
    sheet.addMergedRegion(new CellRangeAddress(rowNumber + 1, rowNumber + 1, 2, 11));
  }
}
