package logProcessor.bo;

public class Configuration {
  //字体云数据共享文件夹URL
  private String fontCloudUrl;
  //OSS共享文件夹数据
  private String OssUrl;
  //域IP
  private String domainIp;
  //域名称
  private String domainName;
  //域账号
  private String userName;
  //域账号密码
  private String userPassword;
  //日志更新间隔
  private int period;

  //FTP IP
  private String ftpHost;
  //FTP 端口
  private int ftpPort;
  //FTP 用户名
  private String ftpUserName;
  //FTP 密码
  private String ftpPassword;

  //CMS DATABASE
  private String cmsDatabaseUrl;
  private String DatabaseUserName;
  private String DatabaseUserPassword;

  public String getFontCloudUrl() {
    return fontCloudUrl;
  }

  public void setFontCloudUrl(String fontCloudUrl) {
    this.fontCloudUrl = fontCloudUrl;
  }

  public String getOssUrl() {
    return OssUrl;
  }

  public void setOssUrl(String ossUrl) {
    OssUrl = ossUrl;
  }

  public String getDomainIp() {
    return domainIp;
  }

  public void setDomainIp(String domainIp) {
    this.domainIp = domainIp;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public String getFtpHost() {
    return ftpHost;
  }

  public void setFtpHost(String ftpHost) {
    this.ftpHost = ftpHost;
  }

  public int getFTPPort() {
    return ftpPort;
  }

  public void setFtpPort(int ftpPort) {
    this.ftpPort = ftpPort;
  }

  public String getFtpUserName() {
    return ftpUserName;
  }

  public void setFtpUserName(String ftpUserName) {
    this.ftpUserName = ftpUserName;
  }

  public String getFtpPassword() {
    return ftpPassword;
  }

  public void setFtpPassword(String ftpPassword) {
    this.ftpPassword = ftpPassword;
  }

  public int getFtpPort() {
    return ftpPort;
  }

  public String getCmsDatabaseUrl() {
    return cmsDatabaseUrl;
  }

  public void setCmsDatabaseUrl(String cmsDatabaseUrl) {
    this.cmsDatabaseUrl = cmsDatabaseUrl;
  }

  public String getDatabaseUserName() {
    return DatabaseUserName;
  }

  public void setDatabaseUserName(String databaseUserName) {
    DatabaseUserName = databaseUserName;
  }

  public String getDatabaseUserPassword() {
    return DatabaseUserPassword;
  }

  public void setDatabaseUserPassword(String databaseUserPassword) {
    DatabaseUserPassword = databaseUserPassword;
  }

  public Configuration() {
    this.domainIp = "172.18.113.3";
    this.domainName = "HOLD";
    this.userName = "cao.zm";
    this.userPassword = "Founder@2011!";
    this.fontCloudUrl = "file://fontfileshare/fontfilesv/手持设备项目/方正字酷/字体云周报/字体云统计数据";
    this.OssUrl = "file://fontfileshare/fontfilesv/手持设备项目/方正字酷/字体云周报/字体云统计数据/OSS下载日志统计";
    this.period = 60;
    this.ftpHost = "192.168.248.202";
    this.ftpPort = 21;
    this.ftpUserName = "sjzt";
    this.ftpPassword = "1233214";
    this.cmsDatabaseUrl = "jdbc:mysql://60.205.142.85:3307/cms?useUnicode=true&autoReconnect=true";
    this.DatabaseUserName = "root";
    this.DatabaseUserPassword = "abcd4321";
  }
}
