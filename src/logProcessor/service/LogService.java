package logProcessor.service;

import static logProcessor.util.DownloadUtil.downloadFromUrl;
import logProcessor.MainEntrance;
import logProcessor.bo.LogBo;
import logProcessor.setting.Constant;
import logProcessor.util.AliyunOSSUtil;
import logProcessor.util.FtpUtil;
import logProcessor.util.Gson.GsonEx;
import logProcessor.util.JarTool;
import logProcessor.util.SharedFoldersUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogService {
  //阿里云日志路径
  private static final String prefix = "http://ztyimg.xiezixiansheng.com/log/ztyimgztyimg";
  private static final String suffix = "-00-00-0001";

  public static boolean processLog(Set<String> set) {
    InputStream is = null;
    Map<Integer, List<LogBo>> objectStreamMap = new HashMap<>();
    FtpUtil ftp = new FtpUtil();
    for (String dateTime : set) {
      try {
        Date date = Constant.STANDARD_DF.parse(dateTime);
        ftp.connect();
        for (int i = 0; i < 24; i++) {
          try {
            is = ftp.getInputStream(Constant.PATH_DF.format(date) + "/" + String.format("%02d", i) + ".log");
            objectStreamMap.put(i, getList(is));
            ftp.closeInputStream(is);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        //开始处理当天数据
        WholeDayDataService dataService = new WholeDayDataService();
        //分析数据
        dataService.analyseLog(objectStreamMap);
        //将结果写入excel并上传至共享文件夹
        dataService.writeStatistic(date);
        MainEntrance.mainFrame.lastDayLogNotification(Constant.STANDARD_DF.format(date));
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      } finally {
        try {
          if (is != null) {
            is.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        ftp.disconnect();
      }
    }
    return true;
  }

  public static Set<String> detectLog(Calendar start, Calendar today) {
    FtpUtil ftp = new FtpUtil();
    ftp.connect();
    Set<String> result = new HashSet<>();
    while (!Constant.STANDARD_DF.format(start.getTime()).equals(Constant.STANDARD_DF.format(today.getTime()))) {
      Set<String> set = ftp.getFileNames(Constant.PATH_DF.format(start.getTime()), "log");
      for (int i = 0; i < 24; i++) {
        if (!set.contains(String.format("%02d", i) + ".log")) {
          result.add(Constant.STANDARD_DF.format(start.getTime()) + "-" + String.format("%02d", i));
        }
      }
      start.add(Calendar.DATE, 1);
    }
    ftp.disconnect();
    return result;
  }

  public static void downloadLog(Set<String> fileSet) {
    FtpUtil ftp = new FtpUtil();
    ftp.connect();
    for (String fileName : fileSet) {
      String filePath = fileName.substring(0, 10);
      String timeSlot = fileName.substring(11, 13);
      InputStream is = null;
      BufferedWriter bw = null;
      try {
        Date date = Constant.STANDARD_DF.parse(filePath);
        filePath = Constant.PATH_DF.format(date.getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }
      String url = AliyunOSSUtil.getSign(prefix + fileName + suffix);
      String tempName = JarTool.getJarDir() + "\\" + filePath + "\\" + timeSlot + ".temp";

      try {
        File file = new File(JarTool.getJarDir() + "\\" + filePath + "\\");
        if (!file.exists()) {
          file.mkdirs();
        }
        is = downloadFromUrl(url);
        List<LogBo> list = filterLog(is);
        bw = new BufferedWriter(new FileWriter(tempName));
        writeLog(list, bw);
        bw.close();
        is.close();
        is = new FileInputStream(tempName);

        ftp.uploadFile(filePath, timeSlot + ".log", is);

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static Set<String> detectCompleteLog(Calendar start, Calendar today) {
    FtpUtil ftp = new FtpUtil();
    ftp.connect();
    Set<String> result = new HashSet<>();
    SharedFoldersUtil sharedFolders = new SharedFoldersUtil();
    sharedFolders.login();
    while (!Constant.STANDARD_DF.format(start.getTime()).equals(Constant.STANDARD_DF.format(today.getTime()))) {
      Set<String> set = ftp.getFileNames(Constant.PATH_DF.format(start.getTime()), "log");
      boolean flag = true;
      for (int i = 0; i < 24; i++) {
        if (!set.contains(String.format("%02d", i) + ".log")) {
          flag = false;
        }
      }

      if (flag) {
        if (!sharedFolders.detectOSSFile(start)) {
          result.add(Constant.STANDARD_DF.format(start.getTime()));
        }
      }
      start.add(Calendar.DATE, 1);
    }
    ftp.disconnect();
    return result;
  }

  //过滤非商城路径请求、非下载请求、非成功请求，并将数据转为对象LogBo
  private static List<LogBo> filterLog(InputStream fis) {
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      isr = new InputStreamReader(fis);
      br = new BufferedReader(isr);
      String line;
      List<LogBo> list = new ArrayList<>();
      while ((line = br.readLine()) != null) {
        if (!line.contains("\"AccessDenied\"") && line.contains("/upload_cms/fonts") && line.contains("\"GetObject\"")) {
          LogBo bo = new LogBo(line);
          list.add(bo);
        }
      }
      return list;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        isr.close();
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  //将日志写成临时文件
  private static void writeLog(List<LogBo> list, BufferedWriter bw) throws IOException {
    for (LogBo bo : list) {
      String json = GsonEx.GSON.toJson(bo);
      bw.write(json);
      bw.newLine();
    }
  }

  private static List<LogBo> getList(InputStream is) {
    if (is == null) {
      return null;
    }
    List<LogBo> list = new ArrayList<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = br.readLine()) != null) {
        list.add(GsonEx.GSON.fromJson(line, LogBo.class));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return list;
  }
}
