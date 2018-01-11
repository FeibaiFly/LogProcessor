package logProcessor.util;

import static logProcessor.MainEntrance.configuration;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FtpUtil {
  /**
   * Description: 向FTP服务器上传文件
   *
   * @param filePath FTP服务器文件存放路径。例如分日期存放：/2015/01/01。
   * @param map      文件流所存储的map，其中包含文件名与InputStream
   * @return
   */
  public static void uploadFile(String filePath, Map<String, InputStream> map) {
    FTPClient ftp = new FTPClient();
    try {
      //连接FTP服务器
      ftp.connect(configuration.getFtpHost());
      //登录
      ftp.login(configuration.getFtpUserName(), configuration.getFtpPassword());
      if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        ftp.disconnect();
      }
      //切换到上传目录
      if (!ftp.changeWorkingDirectory(filePath)) {
        //如果目录不存在创建目录
        String[] dirs = filePath.split("/");
        String tempPath = "";
        for (String dir : dirs) {
          if (null == dir || "".equals(dir)) continue;
          tempPath += "/" + dir;
          if (!ftp.changeWorkingDirectory(tempPath)) {
            if (!ftp.makeDirectory(tempPath)) {
              return;
            } else {
              ftp.changeWorkingDirectory(tempPath);
            }
          }
        }
      }
      //设置上传文件的类型为二进制类型
      ftp.setFileType(FTP.BINARY_FILE_TYPE);
      //上传文件

      for (Map.Entry<String, InputStream> entry : map.entrySet()) {
        if (!ftp.storeFile(entry.getKey(), entry.getValue())) {
          return;
        }
        entry.getValue().close();
      }
      ftp.logout();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (ftp.isConnected()) {
        try {
          ftp.disconnect();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }

  /**
   * Description: 从FTP服务器下载文件
   *
   * @param host       FTP服务器hostname
   * @param port       FTP服务器端口
   * @param username   FTP登录账号
   * @param password   FTP登录密码
   * @param remotePath FTP服务器上的相对路径
   * @param fileName   要下载的文件名
   * @param localPath  下载后保存到本地的路径
   * @return
   */
  public static boolean downloadFile(String host, int port, String username, String password, String remotePath,
                                     String fileName, String localPath) {
    boolean result = false;
    FTPClient ftp = new FTPClient();
    try {
      int reply;
      ftp.connect(host, port);
      // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
      ftp.login(username, password);// 登录
      reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftp.disconnect();
        return result;
      }
      ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
      FTPFile[] fs = ftp.listFiles();
      for (FTPFile ff : fs) {
        if (ff.getName().equals(fileName)) {
          File localFile = new File(localPath + "/" + ff.getName());
          OutputStream is = new FileOutputStream(localFile);
          ftp.retrieveFile(ff.getName(), is);
          is.close();
        }
      }

      ftp.logout();
      result = true;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (ftp.isConnected()) {
        try {
          ftp.disconnect();
        } catch (IOException ioe) {
        }
      }
    }
    return result;
  }

  public static boolean haveDirectory(String date) {
    FTPClient ftp = new FTPClient();
    try {
      ftp.connect(configuration.getFtpHost());
      //登录
      ftp.login(configuration.getFtpUserName(), configuration.getFtpPassword());
      if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        ftp.disconnect();
      }
      return ftp.changeWorkingDirectory(date);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        ftp.logout();
        ftp.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private FTPClient ftp;

  //ftp连接
  public void connect() {
    ftp = new FTPClient();
    try {
      ftp.connect(configuration.getFtpHost());
      ftp.login(configuration.getFtpUserName(), configuration.getFtpPassword());

      if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
        ftp.disconnect();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //ftp断开连接
  public void disconnect() {
    try {
      ftp.logout();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        ftp.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public Set<String> getFileNames(String filePath) {
    return getFileNames(filePath, null);
  }

  //获取某路径下特定后缀名的文件名集合
  public Set<String> getFileNames(String filePath, String ext) {
    try {
      changeWorkingDirectory(filePath);
      Set<String> fileNames = new HashSet<>();
      for (FTPFile file : ftp.listFiles()) {
        if (file.isFile()) {
          if (ext == null) {
            fileNames.add(file.getName());
          } else {
            if (file.getName().endsWith(ext)) {
              fileNames.add(file.getName());
            }
          }
        }
      }
      return fileNames;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void uploadFile(String filePath, String fileName, InputStream is) {
    try {
      changeWorkingDirectory(filePath);
      //设置上传文件的类型为二进制类型
      ftp.setFileType(FTP.BINARY_FILE_TYPE);
      //上传文件
      ftp.storeFile(fileName, is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public InputStream getInputStream(String url) throws IOException {
    return ftp.retrieveFileStream(url);
  }

  public void closeInputStream(InputStream is) throws IOException {
    is.close();
    ftp.completePendingCommand();
  }

  //切换工作目录
  private boolean changeWorkingDirectory(String filePath) throws IOException {
    if (!ftp.changeWorkingDirectory(filePath)) {
      //如果目录不存在创建目录
      String[] dirs = filePath.split("/");
      String tempPath = "";
      for (String dir : dirs) {
        if (null == dir || "".equals(dir)) continue;
        tempPath += "/" + dir;
        if (!ftp.changeWorkingDirectory(tempPath)) {
          if (!ftp.makeDirectory(tempPath)) {
            System.out.println("error!");
          } else {
            ftp.changeWorkingDirectory(tempPath);
          }
        }
      }
    }
    return true;
  }
}
