package tr.bm.earsiv.enums;

public enum InvoiceType {
  SALES("SATIS"),
  RETURN("IADE"),
  WITHHOLDING("TEVKIFAT"),
  WITHHOLDING_RETURN("TEVKIFATIADE"),
  EXEMPTION("ISTISNA"),
  SPECIAL_BASE("OZELMATRAH"),
  EXPORT_REGISTERED("IHRACKAYITLI"),
  ACCOMMODATION_TAX("KONAKLAMAVERGISI");

  private final String value;

  InvoiceType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
