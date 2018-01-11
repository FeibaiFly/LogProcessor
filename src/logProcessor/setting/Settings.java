package logProcessor.setting;

import static logProcessor.MainEntrance.configuration;
import logProcessor.bo.Configuration;
import logProcessor.util.Gson.GsonEx;
import logProcessor.util.JarTool;
import logProcessor.util.ParameterUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class Settings {
  private static FileLock fileLock = null;
  private static RandomAccessFile randomAccessFile = null;
  private static File file = null;

  public static void createConfiguration() {
    BufferedWriter bw = null;

    try {
      bw = new BufferedWriter(new FileWriter(JarTool.getJarDir() + "\\configuration.inf"));
      configuration = new Configuration();
      bw.write(toJson(GsonEx.GSON.toJson(configuration)));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void saveConfiguration() {
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(JarTool.getJarDir() + "\\configuration.inf"));
      bw.write(toJson(GsonEx.GSON.toJson(configuration)));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void detectLog() {
    File[] files = new File(JarTool.getJarDir()).listFiles();
    for (File file : files) {
      if (file.isDirectory() && ParameterUtil.isTempFileName(file.getName())) {
        deleteFile(file);
      }
    }
  }

  //删除文件及子文件
  private static void deleteFile(File file) {
    if (file.isDirectory()) {
      File[] subFiles = file.listFiles();
      if (subFiles != null) {
        for (File subFile : subFiles) {
          deleteFile(subFile);
        }
      }
    }
    file.delete();
  }

  public static void detectConfiguration() {
    File file = new File(JarTool.getJarDir() + "\\configuration.inf");
    BufferedReader br = null;
    InputStreamReader isr = null;
    if (!file.exists()) {
      try {
        file.createNewFile();
        createConfiguration();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      StringBuffer sb = new StringBuffer("");
      String line;
      try {
        isr = new InputStreamReader(new FileInputStream(file));
        br = new BufferedReader(isr);
        while ((line = br.readLine()) != null)
          sb = sb.append(line);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          br.close();
          isr.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      String str = sb.toString().replace(" ", "");
      if (str == null || str.equals("")) {
        createConfiguration();
      } else {
        configuration = GsonEx.GSON.fromJson(str, Configuration.class);
      }
    }
  }

  private static String toJson(String str) {
    StringBuffer stringBuffer = new StringBuffer("");
    int i = 0;
    while (i < str.length()) {
      if (str.charAt(i) == '{') {
        stringBuffer = stringBuffer.append('{' + "\n  ");
        i++;
      } else if (str.charAt(i) == '\"' && str.charAt(i + 1) == ',') {
        stringBuffer = stringBuffer.append("\"," + "\n  ");
        i += 2;
      } else if (Character.isDigit(str.charAt(i)) && str.charAt(i + 1) == ',') {
        stringBuffer = stringBuffer.append(str.charAt(i) + ",\n  ");
        i += 2;
      } else if (str.charAt(i) == '\"' && str.charAt(i + 1) == '}') {
        stringBuffer = stringBuffer.append("\"\n}");
        break;
      } else {
        stringBuffer = stringBuffer.append(str.charAt(i));
        i++;
      }
    }
    return stringBuffer.toString();
  }

  public static boolean detectSingleLock() {
    try {
      file = new File(JarTool.getJarDir() + "\\lock");
      if (!file.exists()) {
        file.createNewFile();
      }
      randomAccessFile = new RandomAccessFile(file, "rw");
      fileLock = randomAccessFile.getChannel().tryLock();
      if (fileLock.isValid()) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static void releaseSingleLock() {
    if (!file.exists()) {
      return;
    } else {
      try {
        if (fileLock != null) {
          fileLock.release();
        }
        if (randomAccessFile != null) {
          randomAccessFile.close();
        }
        file.delete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
