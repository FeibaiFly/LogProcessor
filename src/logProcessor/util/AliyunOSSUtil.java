package logProcessor.util;

import com.aliyun.oss.OSSClient;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class AliyunOSSUtil {
  private final static String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
  private final static String BUCKET_NAME = "ztyimg";
  private final static String ACCESSKEY_ID = "LTAIpQIZjbQkBCfi";
  private final static String SECRET_ACCESSKEY = "rL2W7Q1Gvey9Xb6KI4C6QQSjQRTy4y";
  private final static String IMAGE_URL = "http://ztyimg.xiezixiansheng.com";
  private final static String REAL_URL = "http://ztyimg.oss-cn-beijing.aliyuncs.com";
  private final static int EXP_TIME = 900000;
  private final static String EVN = "test";

  private static OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEY_ID, SECRET_ACCESSKEY);

  public static String sendToOss(InputStream is, String fileName, String path) {

    String key;
    key = EVN + "/" + path + fileName;

    while (key.startsWith("/")) {
      key = key.substring(1, key.length());
    }
    ossClient.putObject(BUCKET_NAME, key, is);
    ossClient.shutdown();

    return IMAGE_URL + key;
  }

  public static String getSign(String url) {
    ossClient = new OSSClient(ENDPOINT, ACCESSKEY_ID, SECRET_ACCESSKEY);
    if (!url.contains("Expires")) {

      String key = url.replace(IMAGE_URL, "");
      while (key.startsWith("/")) {
        key = key.substring(1, key.length());
      }
      // 设置URL过期时间为1小时
      Date expiration = new Date(new Date().getTime() + EXP_TIME);
      // 生成URL
      URL returnUrl = ossClient.generatePresignedUrl(BUCKET_NAME, key, expiration);
      ossClient.shutdown();
      return returnUrl.toString().replace(REAL_URL, IMAGE_URL);
    }
    return url;
  }
}
