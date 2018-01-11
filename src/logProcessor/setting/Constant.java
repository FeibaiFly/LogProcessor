package logProcessor.setting;

import java.text.SimpleDateFormat;

public class Constant {
  //标准日期格式 yyyy-MM-dd
  public static final SimpleDateFormat STANDARD_DF = new SimpleDateFormat("yyyy-MM-dd");

  //路径日期格式 yyyy-MM/dd"
  public static final SimpleDateFormat PATH_DF = new SimpleDateFormat("yyyy-MM/dd");

  //路径日期格式 yyyy-MM"
  public static final SimpleDateFormat YEAR_AND_MONTH_DF = new SimpleDateFormat("yyyy-MM");

  //路径日期格式 dd"
  public static final SimpleDateFormat DAY_DF = new SimpleDateFormat("dd");

  public static final SimpleDateFormat wholeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
