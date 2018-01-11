package logProcessor.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
  public static InputStream downloadFromUrl(String fileUrl) throws IOException {
    URL url = new URL(fileUrl);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //设置超时间为3秒
    conn.setConnectTimeout(3 * 1000);
    //防止屏蔽程序抓取而返回403错误
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

    //得到输入流
    return conn.getInputStream();
  }
}
