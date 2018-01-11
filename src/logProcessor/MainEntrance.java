package logProcessor;

import logProcessor.bo.Configuration;
import logProcessor.gui.MainFrame;
import logProcessor.setting.Settings;

import javax.swing.*;

public class MainEntrance {
  public static MainFrame mainFrame;

  //JFrame宽度以及高度
  public static final int HEIGHT = 242;
  public static final int WIDTH = 300;

  //参数配置
  public static Configuration configuration;

  public static void main(String[] args) {

    if(Settings.detectSingleLock()){
      //程序开启检测
      Settings.detectConfiguration();

      //若文件夹内有临时文件，将其删除
      Settings.detectLog();

      //绘制主程序
      mainFrame = new MainFrame();
    }else {
      JOptionPane.showMessageDialog(null, "已经开启一个日志进程，请勿重复开启。", "警告", JOptionPane.WARNING_MESSAGE);
    }
  }
}
