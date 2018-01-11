package logProcessor.event;

import logProcessor.MainEntrance;
import logProcessor.setting.LogTask;
import logProcessor.util.CalendarUtil;

import java.util.Date;
import java.util.Timer;

public class LogEvent {

  public static Timer timer;

  //开启定时任务
  public synchronized static void monitor() {
    timer = new Timer();
    timer.schedule(new LogTask(), CalendarUtil.getNextTime(), MainEntrance.configuration.getPeriod() * 1000 * 60);
    //timer.schedule(new LogTask(), new Date(), 1000 * 5);
    //timer.schedule(new LogTask(), new Date(), 1000 * 5 * 60);
  }
}