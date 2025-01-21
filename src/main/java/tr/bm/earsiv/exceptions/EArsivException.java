package tr.bm.earsiv.exceptions;

public class EArsivException extends Exception {
  private final String code;

  public EArsivException(String message) {
    super(message);
    this.code = "0";
  }

  public EArsivException(String code, String message) {
    super(message);
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
