package logProcessor.setting;

import logProcessor.MainEntrance;
import logProcessor.service.LogService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Set;
import java.util.TimerTask;

public class LogTask extends TimerTask {

  //private static boolean result = false;

  @Override
  public void run() {
    //将状态栏修改为忙碌状态
    MainEntrance.mainFrame.switchStatusJLabel();

    ////获取并处理前一天的日志
    //Calendar date = Calendar.getInstance();
    //date.add(Calendar.DATE, -1);
    //if (!result) {
    //  result = FtpUtil.haveDirectory(Constant.PATH_DF.format(date.getTime()));
    //}
    //
    //if (!result) {
    //  result = LogService.processLog(date, Constant.PATH_DF.format(date.getTime()));
    //  //若周一执行本程序，将周末的数据也统计并上传
    //  if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
    //    date.add(Calendar.DATE, -1);
    //    LogService.processLog(date, Constant.PATH_DF.format(date.getTime()));
    //    date.add(Calendar.DATE, -1);
    //    LogService.processLog(date, Constant.PATH_DF.format(date.getTime()));
    //  }
    //}

    //检测当月的日志,若日期为当月前五天，多检测上一月的最后15天
    boolean flag = false;
    Calendar date = Calendar.getInstance();
    Calendar start = Calendar.getInstance();
    start.add(Calendar.DATE, -5);
    if (start.get(Calendar.MONTH) != date.get(Calendar.MONTH)) {
      flag = true;
    }
    start.set(Calendar.DAY_OF_MONTH, 1);
    start.set(Calendar.MONTH, date.get(Calendar.MONTH));
    start.set(Calendar.YEAR, date.get(Calendar.YEAR));
    if (flag) {
      start.add(Calendar.DATE, -15);
    }

    String startTime = Constant.STANDARD_DF.format(start.getTime());

    //检测FTP日志不全的日期，返回Set；下载缺失日志
    Set<String> set = LogService.detectLog(start, date);
    LogService.downloadLog(set);

    try {
      start.setTime(Constant.STANDARD_DF.parse(startTime));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    //检测FTP日志完整的日期，返回Set；检测数据统计是否生成，若缺失，则生成统计文件
    Set<String> result = LogService.detectCompleteLog(start, date);
    LogService.processLog(result);

    //定时任务完成，将状态栏修改为空闲状态
    MainEntrance.mainFrame.switchStatusJLabel();
  }
}