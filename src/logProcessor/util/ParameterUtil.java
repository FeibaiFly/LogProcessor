package logProcessor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterUtil {
  public static boolean isIpAddress(String address) {
    if (address.length() < 7 || address.length() > 15 || "".equals(address)) {
      return false;
    }
    /**
     * 判断IP格式和范围
     */
    String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

    Pattern pat = Pattern.compile(regex);

    Matcher mat = pat.matcher(address);

    return mat.find();
  }

  public static boolean isTempFileName(String fileName) {
    if (fileName.length() == 7) {
      String regex = "[0-9][0-9][0-9][0-9]-[0,1][0-9]";
      Pattern pat = Pattern.compile(regex);
      Matcher mat = pat.matcher(fileName);
      return mat.find();
    }
    return false;
  }
}
