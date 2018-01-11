package logProcessor.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogBo {

  /**
   * Remote IP
   * 请求发起的IP地址（Proxy代理或用户防火墙可能会屏蔽该字段）
   */
  private String ip;

  /**
   * Time
   * OSS收到请求的时间
   */
  private Date time;

  /**
   * 请求时区
   */
  private String timeZone;

  /**
   * Request-URI
   * 用户请求的URI（包括query-string）
   */
  private String requestURI;

  /**
   * 下载的文件名
   */
  private String downloadFile;

  /**
   * http方法
   */
  private String httpMethod;

  /**
   * 请求协议
   */
  private String protocol;

  /**
   * HTTP Status
   * OSS返回的HTTP状态码
   */
  private int returnCode;

  /**
   * SentBytes
   * 用户从OSS下载的流量
   */
  private int flux;

  /**
   * RequestTime (ms)
   * 完成本次请求的时间（毫秒）
   */
  private int requestTime;

  /**
   * Referer
   * 请求的HTTP Referer
   */
  private String referer;

  /**
   * User-Agent
   * HTTP的User-Agent头
   */
  private String userAgent;

  /**
   * Requester Aliyun ID
   * 请求者的阿里云ID，匿名为null
   */
  private String requestId;

  /**
   * ObjectSize
   * Object大小
   */
  private int objectSize;

  /**
   * Server Cost Time (ms)
   * OSS服务器处理本次请求所花的时间（毫秒）
   */
  private int costTime;

  /**
   * Request Length
   * 用户请求的长度（Byte）
   */
  private int requestLength;

  /**
   * Sync Request
   * 是否是CDN回源请求；
   */
  private boolean isCdn;

  public LogBo() {
  }

  public LogBo(String info) {

    String[] information = info.split(" - - ");
    this.ip = information[0].replace(" ", "");

    information = information[1].split("] ");
    String[] fragments = information[0].split(" ");
    this.timeZone = fragments[1];

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
    try {
      this.time = sdf.parse(information[0].replace("[", ""));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    fragments = information[1].split(" \\d+ \\d+ \\d+ ");
    this.requestURI = fragments[0].substring(1, fragments[0].length() - 1);
    fragments = this.requestURI.split(" ");
    this.httpMethod = fragments[0];
    this.protocol = fragments[2];
    fragments = fragments[1].split("\\?Expires=")[0].split("/");
    this.downloadFile = fragments[fragments.length - 1];

    information = information[1].split(this.protocol + "\" ")[1].split(" ");
    this.returnCode = Integer.parseInt(information[0]);
    this.flux = Integer.parseInt(information[1]);
    this.requestTime = Integer.parseInt(information[2]);
    this.referer = information[3].substring(1, information[3].length() - 1);
    for (int i = 4; i < information.length - 15; i++) {
      if (userAgent == null) {
        this.userAgent = information[i].replace("\"", "");
      } else {
        this.userAgent = this.userAgent + " " + information[i].replace("\"", "");
      }
    }
    this.requestId = information[information.length - 12].equals("\"-\"") ? null
            : information[information.length - 12].substring(1, information[information.length - 12].length() - 2);
    this.objectSize = information[information.length - 8].equals("-") ? 0 :
            Integer.parseInt(information[information.length - 8]);
    this.costTime = information[information.length - 7].equals("-") ? 0 :Integer.parseInt(information[information.length - 7]);
    this.requestLength = information[information.length - 5].equals("-") ? 0 :Integer.parseInt(information[information.length - 5]);
    this.isCdn = information[information.length - 2].equals("\"cdn\"");
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getRequestURI() {
    return requestURI;
  }

  public void setRequestURI(String requestURI) {
    this.requestURI = requestURI;
  }

  public String getDownloadFile() {
    return downloadFile;
  }

  public void setDownloadFile(String downloadFile) {
    this.downloadFile = downloadFile;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public int getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(int returnCode) {
    this.returnCode = returnCode;
  }

  public int getFlux() {
    return flux;
  }

  public void setFlux(int flux) {
    this.flux = flux;
  }

  public int getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(int requestTime) {
    this.requestTime = requestTime;
  }

  public String getReferer() {
    return referer;
  }

  public void setReferer(String referer) {
    this.referer = referer;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public int getObjectSize() {
    return objectSize;
  }

  public void setObjectSize(int objectSize) {
    this.objectSize = objectSize;
  }

  public int getCostTime() {
    return costTime;
  }

  public void setCostTime(int costTime) {
    this.costTime = costTime;
  }

  public int getRequestLength() {
    return requestLength;
  }

  public void setRequestLength(int requestLength) {
    this.requestLength = requestLength;
  }

  public boolean isCdn() {
    return isCdn;
  }

  public void setCdn(boolean cdn) {
    isCdn = cdn;
  }

  @Override
  public String toString() {
    return "LogBo{" +
            "ip='" + ip + '\'' +
            ", time=" + time +
            ", timeZone='" + timeZone + '\'' +
            ", requestURI='" + requestURI + '\'' +
            ", downloadFile='" + downloadFile + '\'' +
            ", httpMethod='" + httpMethod + '\'' +
            ", protocol='" + protocol + '\'' +
            ", returnCode=" + returnCode +
            ", flux=" + flux +
            ", requestTime=" + requestTime +
            ", referer='" + referer + '\'' +
            ", userAgent='" + userAgent + '\'' +
            ", requestId='" + requestId + '\'' +
            ", objectSize=" + objectSize +
            ", costTime=" + costTime +
            ", requestLength=" + requestLength +
            ", isCdn=" + isCdn +
            '}';
  }
}
