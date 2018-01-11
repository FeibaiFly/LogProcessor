package logProcessor.event;

import logProcessor.MainEntrance;
import logProcessor.setting.Settings;

public class FrameEvent {
  //开始按钮事件
  public static void confirmEvent(String domainIp, String domainName, String userName, String password) {
    MainEntrance.configuration.setDomainIp(domainIp);
    MainEntrance.configuration.setDomainName(domainName);
    MainEntrance.configuration.setUserName(userName);
    MainEntrance.configuration.setUserPassword(password);

    //存储配置信息
    Settings.saveConfiguration();

    LogEvent.monitor();

    MainEntrance.mainFrame.setEnableExitMenu(false);
  }

  //停止按钮事件
  public static void stopEvent() {
    LogEvent.timer.cancel();
    MainEntrance.mainFrame.setEnableExitMenu(true);
  }

  //退出按钮事件
  public static void exitEvent() {
    Settings.saveConfiguration();
    Settings.releaseSingleLock();
    System.exit(0);
  }
}
