package logProcessor.bo;

public class DownloadBo {
  private int total;
  private int sugarOrSoap;
  private int QEZBIos;
  private int otherUserMobile;
  private int pcOrOthers;

  public void totalIncrement() {
    total++;
  }

  public void sugarOrSoapIncrement() {
    sugarOrSoap++;
  }

  public void QEZBIosIncrement() {
    QEZBIos++;
  }

  public void otherUserMobileIncrement() {
    otherUserMobile++;
  }

  public void pcOrOthersIncrement() {
    pcOrOthers++;
  }

  public int getTotal() {
    return total;
  }

  public int getSugarOrSoap() {
    return sugarOrSoap;
  }

  public int getQEZBIos() {
    return QEZBIos;
  }

  public int getOtherUserMobile() {
    return otherUserMobile;
  }

  public int getPcOrOthers() {
    return pcOrOthers;
  }
}
