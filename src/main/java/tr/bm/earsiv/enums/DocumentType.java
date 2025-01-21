package tr.bm.earsiv.enums;

public enum DocumentType {
  HTML("HTML"),
  PDF("PDF");

  private final String value;

  DocumentType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
