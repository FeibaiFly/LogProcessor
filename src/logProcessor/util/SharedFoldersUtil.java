package logProcessor.util;

import static logProcessor.MainEntrance.configuration;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import jcifs.smb.SmbSession;
import logProcessor.setting.Constant;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

public class SharedFoldersUtil {
  public static void uploadFile(Workbook workbook, Date date) {
    SmbFileOutputStream smbFileOutputStream = null;
    try {
      UniAddress dc = UniAddress.getByName(configuration.getDomainIp());
      NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(configuration.getDomainName(),
              configuration.getUserName(), configuration.getUserPassword());
      SmbSession.logon(dc, authentication);
      String path = configuration.getOssUrl() + "/" + Constant.YEAR_AND_MONTH_DF.format(date.getTime());
      SmbFile smbDirectory = new SmbFile(path, authentication);
      if (!smbDirectory.exists()) {
        smbDirectory.mkdir();
      }

      SmbFile smbFile = new SmbFile(path + "/" + Constant.STANDARD_DF.format(date.getTime()) + ".xlsx", authentication);
      smbFileOutputStream = new SmbFileOutputStream(smbFile);
      workbook.write(smbFileOutputStream);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        smbFileOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  //public static void detectFile(String filename) {
  //  UniAddress dc = UniAddress.getByName(configuration.getDomainIp());
  //  NtlmPasswordAuthentication authentication
  //          = new NtlmPasswordAuthentication(configuration.getDomainName(),
  //          configuration.getUserName(), configuration.getUserPassword());
  //  SmbSession.logon(dc, authentication);
  //  SmbFile file = new SmbFile(configuration.);
  //}

  private UniAddress dc;
  private NtlmPasswordAuthentication authentication;

  public void login() {
    try {
      this.dc = UniAddress.getByName(configuration.getDomainIp());
      this.authentication = new NtlmPasswordAuthentication(configuration.getDomainName(),
              configuration.getUserName(), configuration.getUserPassword());
      SmbSession.logon(dc, authentication);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (SmbException e) {
      e.printStackTrace();
    }
  }

  public boolean detectOSSFile(Calendar date) {
    String path = configuration.getOssUrl() + "/" + Constant.YEAR_AND_MONTH_DF.format(date.getTime());
    try {
      SmbFile file = new SmbFile(path + "/" + Constant.STANDARD_DF.format(date.getTime()) + ".xlsx", authentication);
      return file.exists();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
