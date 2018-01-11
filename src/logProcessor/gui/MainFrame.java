package logProcessor.gui;

import logProcessor.MainEntrance;
import logProcessor.event.FrameEvent;
import logProcessor.gui.style.ComponentStyle;
import logProcessor.util.ParameterUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;

public class MainFrame extends JFrame {

  private LogAnalyzeSystemTray tray;

  private static final long serialVersionUID = -4761834946498721975L;

  private boolean workingStatus = false;

  private JTextField domainIpText;
  private JTextField domainNameText;
  private JTextField userNameText;
  private JPasswordField userPasswordText;

  private JLabel domainIpLabel;
  private JLabel domainNameLabel;
  private JLabel userNameLabel;
  private JLabel userPasswordLabel;
  private JLabel statusLabel;
  private boolean status = false;

  private JButton switchButton;
  private JButton exitButton;

  public void setEnableExitMenu(boolean trigger) {
    this.tray.exit.setEnabled(trigger);
  }

  public void switchStatusJLabel() {
    if (status) {
      status = false;
      statusLabel.setBackground(Color.GREEN);
      statusLabel.setText("空闲中...");
    } else {
      status = true;
      statusLabel.setText("正在处理日志，请不要关闭...");
      statusLabel.setBackground(Color.RED);
    }
  }

  public void lastDayLogNotification(String date) {
    tray.trayicon.displayMessage("消息", date + "的日志已经下载并统计完成。", TrayIcon.MessageType.INFO);
  }

  public MainFrame() {
    super("手迹云字体日志处理工具");

    //获取运行图标
    try {
      InputStream is = this.getClass().getResourceAsStream("/resource/favicon.ico");
      this.setIconImage(ImageIO.read(is));
    } catch (IOException e) {
      e.printStackTrace();
    }

    //系统托盘
    tray = new LogAnalyzeSystemTray();

    try {
      SystemTray.getSystemTray().add(tray.trayicon);
    } catch (AWTException e1) {
      e1.printStackTrace();
    }

    //窗口按钮监听
    this.addWindowListener(new WindowListener() {
      @Override
      public void windowOpened(WindowEvent e) {

      }

      @Override
      public void windowClosing(WindowEvent e) {
        setVisible(false);
      }

      @Override
      public void windowClosed(WindowEvent e) {

      }

      @Override
      public void windowIconified(WindowEvent e) {
        setVisible(false);
      }

      @Override
      public void windowDeiconified(WindowEvent e) {

      }

      @Override
      public void windowActivated(WindowEvent e) {

      }

      @Override
      public void windowDeactivated(WindowEvent e) {

      }
    });

    //绘制面板
    draw();

    int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    this.setBounds((screenWidth - MainEntrance.WIDTH) / 2, (screenHeight - MainEntrance.HEIGHT) / 2, MainEntrance.WIDTH, MainEntrance.HEIGHT);
    //设置不使用任何布局
    this.setLayout(null);
    this.setResizable(false);
    this.setVisible(true);
  }

  private void draw() {
    //绘制标签
    domainIpLabel = new JLabel("域IP", SwingConstants.CENTER);
    domainIpLabel.setBounds(new Rectangle(10, 10, 70, 24));
    ComponentStyle.fontStyle(domainIpLabel);
    domainNameLabel = new JLabel("域名称", SwingConstants.CENTER);
    domainNameLabel.setBounds(new Rectangle(10, 44, 70, 24));
    ComponentStyle.fontStyle(domainNameLabel);
    userNameLabel = new JLabel("域账号", SwingConstants.CENTER);
    userNameLabel.setBounds(new Rectangle(10, 78, 70, 24));
    ComponentStyle.fontStyle(userNameLabel);
    userPasswordLabel = new JLabel("密码", SwingConstants.CENTER);
    userPasswordLabel.setBounds(new Rectangle(10, 112, 70, 24));
    ComponentStyle.fontStyle(userPasswordLabel);
    statusLabel = new JLabel("空闲中...", SwingConstants.CENTER);
    statusLabel.setBounds(new Rectangle(10, 146, 270, 24));
    statusLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    statusLabel.setBackground(Color.GREEN);
    statusLabel.setOpaque(true);

    //绘制文本框
    domainIpText = new JTextField(MainEntrance.configuration.getDomainIp());
    domainIpText.setBounds(new Rectangle(90, 10, 190, 24));
    //设置文本框离焦事件，当离焦时，校验输入内容是否为IP地址
    domainIpText.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (!ParameterUtil.isIpAddress(domainIpText.getText())) {
          domainIpText.setBackground(Color.RED);
          JOptionPane.showMessageDialog(null, "IP地址填写不正确", "填写错误", JOptionPane.ERROR_MESSAGE);
          domainIpText.grabFocus();
        } else {
          domainIpText.setBackground(Color.WHITE);
        }
      }
    });

    domainNameText = new JTextField(MainEntrance.configuration.getDomainName());
    domainNameText.setBounds(new Rectangle(90, 44, 190, 24));

    userNameText = new JTextField(MainEntrance.configuration.getUserName());
    userNameText.setBounds(new Rectangle(90, 78, 190, 24));

    userPasswordText = new JPasswordField(MainEntrance.configuration.getUserPassword());
    userPasswordText.setBounds(new Rectangle(90, 112, 190, 24));

    //绘制按钮
    switchButton = new JButton("开  始");
    ComponentStyle.fontStyle(switchButton);
    switchButton.setBounds(new Rectangle(10, 180, 130, 24));
    switchButton.addActionListener((e) -> switchButtonListener());
    exitButton = new JButton("退  出");
    ComponentStyle.fontStyle(exitButton);
    exitButton.setBounds(new Rectangle(150, 180, 130, 24));
    exitButton.addActionListener((e) -> FrameEvent.exitEvent());

    //将控件添加到容器中
    this.add(switchButton);
    this.add(exitButton);
    this.add(domainIpLabel);
    this.add(domainNameLabel);
    this.add(userNameLabel);
    this.add(userPasswordLabel);
    this.add((statusLabel));
    this.add(domainIpText);
    this.add(domainNameText);
    this.add(userNameText);
    this.add(userPasswordText);
  }

  private void switchButtonListener() {
    if (workingStatus) {
      FrameEvent.stopEvent();
      workingStatus = false;
      domainIpText.setEnabled(true);
      domainNameText.setEnabled(true);
      userNameText.setEnabled(true);
      userPasswordText.setEnabled(true);
      switchButton.setText("开  始");
    } else {
      FrameEvent.confirmEvent(this.domainIpText.getText(), this.domainNameText.getText(),
              this.userNameText.getText(), new String(this.userPasswordText.getPassword()));
      workingStatus = true;
      domainIpText.setEnabled(false);
      domainNameText.setEnabled(false);
      userNameText.setEnabled(false);
      userPasswordText.setEnabled(false);
      switchButton.setText("停  止");
    }
  }

  class LogAnalyzeSystemTray implements ActionListener {
    private TrayIcon trayicon;
    private PopupMenu popup;
    private MenuItem showWindow;
    private MenuItem exit;

    private LogAnalyzeSystemTray() {
      init();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == showWindow) {
        MainEntrance.mainFrame.setVisible(true);
      } else if (e.getSource() == exit) {
        FrameEvent.exitEvent();
      }
    }

    private void init() {
      popup = new PopupMenu();

      showWindow = new MenuItem("显示窗口");
      showWindow.addActionListener(this);
      exit = new MenuItem("退出");
      exit.addActionListener(this);

      popup.add(showWindow);
      popup.add(exit);

      if (SystemTray.isSupported()) {
        try {
          trayicon = new TrayIcon(ImageIO.read(this.getClass().getResourceAsStream("/resource/favicon.ico")), "商城日志统计", popup);
        } catch (IOException e1) {
          System.out.println("图片加载失败！");
        }
        trayicon.setImageAutoSize(true);

        trayicon.addMouseListener(new MouseAdapter() {
          //为托盘添加鼠标事件,双击则打开程序界面
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              MainEntrance.mainFrame.setVisible(true);
            }
          }
        });
      }
    }
  }
}