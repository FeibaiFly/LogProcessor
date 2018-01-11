package logProcessor.util;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
  public static Date getNextTime() {
    Calendar date = Calendar.getInstance();
    date.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY) + 1);
    date.set(Calendar.MINUTE, 30);
    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
    return date.getTime();
  }
}
