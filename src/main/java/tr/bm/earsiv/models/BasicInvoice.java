package tr.bm.earsiv.models;

import java.util.UUID;
import org.json.JSONObject;

public class BasicInvoice {
  private String documentNumber; // belgeNumarasi
  private String taxOrIdNumber; // aliciVknTckn
  private String recipientName; // aliciUnvanAdSoyad
  private String documentDate; // belgeTarihi
  private String documentType; // belgeTuru
  private String approvalStatus; // onayDurumu
  private UUID uuid; // ettn

  public String getDocumentNumber() {
    return documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public String getTaxOrIdNumber() {
    return taxOrIdNumber;
  }

  public void setTaxOrIdNumber(String taxOrIdNumber) {
    this.taxOrIdNumber = taxOrIdNumber;
  }

  public String getRecipientName() {
    return recipientName;
  }

  public void setRecipientName(String recipientName) {
    this.recipientName = recipientName;
  }

  public String getDocumentDate() {
    return documentDate;
  }

  public void setDocumentDate(String documentDate) {
    this.documentDate = documentDate;
  }

  public String getDocumentType() {
    return documentType;
  }

  public void setDocumentType(String documentType) {
    this.documentType = documentType;
  }

  public String getApprovalStatus() {
    return approvalStatus;
  }

  public void setApprovalStatus(String approvalStatus) {
    this.approvalStatus = approvalStatus;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public JSONObject toJsonObject() {
    return new JSONObject()
        .put("belgeNumarasi", documentNumber)
        .put("aliciVknTckn", taxOrIdNumber)
        .put("aliciUnvanAdSoyad", recipientName)
        .put("belgeTarihi", documentDate)
        .put("belgeTuru", documentType)
        .put("onayDurumu", approvalStatus)
        .put("ettn", uuid.toString())
        .put("faturaUuid", uuid.toString());
  }

  public static BasicInvoice fromJsonObject(JSONObject json) {
    BasicInvoice invoice = new BasicInvoice();
    invoice.setDocumentNumber(getFirstNonEmpty(json, "belgeNumarasi", "faturaTipi"));
    invoice.setTaxOrIdNumber(getFirstNonEmpty(json, "aliciVknTckn", "vknTckn"));
    invoice.setRecipientName(getFirstNonEmpty(json, "aliciUnvanAdSoyad", "aliciUnvan"));
    invoice.setDocumentDate(getFirstNonEmpty(json, "belgeTarihi", "faturaTarihi"));
    invoice.setDocumentType(json.optString("belgeTuru"));
    invoice.setApprovalStatus(json.optString("onayDurumu"));
    String uuidString = json.has("ettn") ? json.getString("ettn") : json.getString("faturaUuid");
    invoice.setUuid(UUID.fromString(uuidString));
    return invoice;
  }

  private static String getFirstNonEmpty(JSONObject json, String... keys) {
    for (String key : keys) {
      if (json.has(key) && !json.optString(key).isEmpty()) {
        return json.getString(key);
      }
    }
    return "";
  }

  @Override
  public String toString() {
    return String.format(
        "Invoice{documentNumber='%s', recipient='%s', date='%s', status='%s', uuid='%s'}",
        documentNumber, recipientName, documentDate, approvalStatus, uuid);
  }
}
