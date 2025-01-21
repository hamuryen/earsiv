package tr.bm.earsiv.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.bm.earsiv.enums.InvoiceType;
import tr.bm.earsiv.enums.UnitType;
import tr.bm.earsiv.enums.VatRate;
import tr.bm.earsiv.exceptions.EArsivException;
import tr.bm.earsiv.models.BasicInvoice;
import tr.bm.earsiv.models.Invoice;
import tr.bm.earsiv.models.InvoiceItem;
import tr.bm.earsiv.utilities.HtmlToPdfConverter;
import tr.bm.earsiv.utilities.HttpUtilities;

public class EArsivService {
  private static final Logger logger = LoggerFactory.getLogger(EArsivService.class);

  private static final String TEST_URL = "https://earsivportaltest.efatura.gov.tr";
  private static final String PROD_URL = "https://earsivportal.efatura.gov.tr";
  private static final String SERVICE_PATH = "/earsiv-services";

  private final String baseUrl;
  private final boolean isTest;
  private String token = null;
  private String callId;
  private Integer requestCount;

  public EArsivService() {
    this(false);
  }

  public EArsivService(boolean isTest) {
    logger.info("Initializing EArsivService in {} mode", isTest ? "TEST" : "PRODUCTION");
    this.isTest = isTest;
    this.baseUrl = isTest ? TEST_URL : PROD_URL;
    this.callId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 13);
    this.requestCount = 0;
  }

  private String generateCallId() {
    return this.callId + "-" + this.requestCount++;
  }

  private String getToken() throws EArsivException {
    checkToken();
    return this.token;
  }

  private void checkToken() throws EArsivException {
    if (this.token == null) {
      logger.error("Token validation failed: Token is null");
      throw new EArsivException("Token not found. Please login first.");
    }
  }

  private JSONObject createRequestBody(String command, String pageName, JSONObject parameters)
      throws EArsivException {
    logger.debug("Creating request body - Command: {}, Page: {}", command, pageName);
    JSONObject body =
        new JSONObject()
            .put("cmd", command)
            .put("callid", generateCallId())
            .put("pageName", pageName)
            .put("token", getToken())
            .put("jp", parameters.toString());
    logger.trace("Created request body: {}", body);
    return body;
  }

  private JSONObject postDispatchRequest(JSONObject requestBody) throws EArsivException {
    return HttpUtilities.post(baseUrl + SERVICE_PATH + "/dispatch", requestBody, null);
  }

  private JSONObject postDispatchRequest(JSONObject requestBody, String successKey)
      throws EArsivException {
    return HttpUtilities.post(baseUrl + SERVICE_PATH + "/dispatch", requestBody, successKey);
  }

  public void login(String username, String password) throws EArsivException {
    logger.info("Attempting login for user: {}", username);
    String assoscmd = isTest ? "login" : "anologin";
    JSONObject requestBody =
        new JSONObject()
            .put("assoscmd", assoscmd)
            .put("rtype", "json")
            .put("userid", username)
            .put("sifre", password)
            .put("sifre2", password)
            .put("parola", "1");

    try {
      JSONObject response =
          HttpUtilities.post(baseUrl + SERVICE_PATH + "/assos-login", requestBody, "token");
      if (!response.has("token")) {
        logger.error("Login failed: Token not found in response");
        throw new EArsivException("Token not found");
      }
      this.token = response.getString("token");
      logger.info("Login successful");
    } catch (EArsivException e) {
      logger.error("Login failed: {}", e.getMessage());
      throw e;
    }
  }

  public void logout() throws EArsivException {
    JSONObject requestBody =
        new JSONObject().put("assoscmd", "logout").put("rtype", "json").put("token", getToken());
    JSONObject response =
        HttpUtilities.post(baseUrl + SERVICE_PATH + "/assos-login", requestBody, null);
    this.token = null;
  }

  public JSONObject getRecipientData(String taxOrIdNumber) throws EArsivException {
    return postDispatchRequest(
            createRequestBody(
                "SICIL_VEYA_MERNISTEN_BILGILERI_GETIR",
                "RG_BASITFATURA",
                new JSONObject().put("vknTcknn", taxOrIdNumber)))
        .getJSONObject("data");
  }

  public JSONObject createDraftInvoice(Invoice invoice) throws EArsivException {
    logger.info("Creating draft invoice with UUID: {}", invoice.getUuid());
    try {
      JSONObject response =
          postDispatchRequest(
              createRequestBody(
                  "EARSIV_PORTAL_FATURA_OLUSTUR", "RG_BASITFATURA", invoice.toJsonObject()),
              "Faturanız başarıyla oluşturulmuştur");
      logger.info("Draft invoice created successfully");
      return response;
    } catch (EArsivException e) {
      logger.error("Failed to create draft invoice: {}", e.getMessage());
      throw e;
    }
  }

  public UUID createQuickInvoiceForSales(
      String taxOrIdNumber,
      String itemName,
      UnitType itemUnitType,
      double itemQuantity,
      double itemUnitPrice,
      VatRate itemVatRate,
      double discountRate)
      throws EArsivException {
    return createQuickInvoiceForSales(
        taxOrIdNumber,
        itemName,
        itemUnitType,
        itemQuantity,
        itemUnitPrice,
        itemVatRate,
        discountRate,
        "",
        "NİHAİ",
        "TÜKETİCİ",
        "",
        "");
  }

  public UUID createQuickInvoiceForSales(
      String taxOrIdNumber,
      String itemName,
      UnitType itemUnitType,
      double itemQuantity,
      double itemUnitPrice,
      VatRate itemVatRate,
      double discountRate,
      String title,
      String name,
      String surname,
      String taxOffice,
      String address)
      throws EArsivException {
    JSONObject recipient = getRecipientData(taxOrIdNumber);
    Invoice invoice =
        new Invoice.Builder()
            .invoiceType(InvoiceType.SALES)
            .recipient(recipient, taxOrIdNumber, taxOffice, title, name, surname)
            .street(address)
            .addItem(
                new InvoiceItem.Builder()
                    .name(itemName)
                    .unitType(itemUnitType)
                    .quantity(itemQuantity)
                    .unitPrice(itemUnitPrice)
                    .vatRate(itemVatRate)
                    .discountRate(discountRate)
                    .build())
            .build();
    createDraftInvoice(invoice);
    return invoice.getUuid();
  }

  public List<BasicInvoice> getInvoiceByDateRange(LocalDate startDate, LocalDate endDate)
      throws EArsivException {
    logger.info("Fetching invoices between {} and {}", startDate, endDate);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    JSONObject parameters =
        new JSONObject()
            .put("baslangic", startDate.format(formatter))
            .put("bitis", endDate.format(formatter))
            .put("hangiTip", "5000/30000");

    try {
      JSONArray jsonArray =
          postDispatchRequest(
                  createRequestBody(
                      "EARSIV_PORTAL_TASLAKLARI_GETIR", "RG_BASITTASLAKLAR", parameters))
              .getJSONArray("data");

      List<BasicInvoice> invoices = parseInvoiceList(jsonArray);
      logger.info("Found {} invoices in date range", invoices.size());
      return invoices;
    } catch (EArsivException e) {
      logger.error("Failed to fetch invoices: {}", e.getMessage());
      throw e;
    }
  }

  private List<BasicInvoice> parseInvoiceList(JSONArray jsonArray) {
    List<BasicInvoice> invoices = new ArrayList<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      invoices.add(BasicInvoice.fromJsonObject(jsonArray.getJSONObject(i)));
    }
    return invoices;
  }

  public BasicInvoice getBasicInvoiceByDateAndId(LocalDate date, UUID id) throws EArsivException {
    List<BasicInvoice> invoices = getInvoiceByDateRange(date, date);
    return invoices.stream()
        .filter(invoice -> invoice.getUuid().equals(id))
        .findFirst()
        .orElse(null);
  }

  public JSONObject getInvoiceById(UUID id) throws EArsivException {
    return postDispatchRequest(
            createRequestBody(
                "EARSIV_PORTAL_FATURA_GETIR",
                "RG_BASITFATURA",
                new JSONObject().put("ettn", id.toString())))
        .getJSONObject("data");
  }

  public String getInvoiceAsHtml(UUID id) throws EArsivException {
    return getInvoiceAsHtml(id, "Onaylandı");
  }

  public String getInvoiceAsHtml(UUID id, String status) throws EArsivException {
    return postDispatchRequest(
            createRequestBody(
                "EARSIV_PORTAL_FATURA_GOSTER",
                "RG_BASITTASLAKLAR",
                new JSONObject().put("ettn", id.toString()).put("onayDurumu", status)))
        .getString("data");
  }

  public byte[] getInvoiceAsPdf(UUID id) throws EArsivException {
    String html = getInvoiceAsHtml(id);
    if (html == null || html.isEmpty()) {
      throw new EArsivException("HTML content is empty");
    }
    return HtmlToPdfConverter.convertHtmlToPdf(html);
  }

  public void cancelInvoice(List<BasicInvoice> invoices) throws EArsivException {
    cancelInvoice(invoices, "Hatalı işlem");
  }

  public void cancelInvoice(List<BasicInvoice> invoices, String reason) throws EArsivException {
    logger.info("Cancelling {} invoices with reason: {}", invoices.size(), reason);
    JSONArray idArray = new JSONArray();
    invoices.stream().map(BasicInvoice::toJsonObject).forEach(idArray::put);

    JSONObject parameters = new JSONObject().put("silinecekler", idArray).put("aciklama", reason);

    try {
      postDispatchRequest(
          createRequestBody("EARSIV_PORTAL_FATURA_SIL", "RG_BASITTASLAKLAR", parameters));
      logger.info("Successfully cancelled {} invoices", invoices.size());
    } catch (EArsivException e) {
      logger.error("Failed to cancel invoices: {}", e.getMessage());
      throw e;
    }
  }

  public String getPhoneNumber() throws EArsivException {
    return postDispatchRequest(
            createRequestBody(
                "EARSIV_PORTAL_TELEFONNO_SORGULA", "RG_BASITTASLAKLAR", new JSONObject()))
        .getJSONObject("data")
        .getString("telefon");
  }

  public String sendSignSMSCode() throws EArsivException {
    return sendSignSMSCode(null);
  }

  public String sendSignSMSCode(String phone) throws EArsivException {
    logger.info("Sending SMS code to phone: {}", phone);
    if (phone == null || phone.isEmpty()) {
      logger.debug("Phone number not provided, fetching from system");
      phone = getPhoneNumber();
    }
    try {
      String oid =
          postDispatchRequest(
                  createRequestBody(
                      "EARSIV_PORTAL_SMSSIFRE_GONDER",
                      "RG_SMSONAY",
                      new JSONObject().put("CEPTEL", phone).put("KCEPTEL", false).put("TIP", "")))
              .getJSONObject("data")
              .getString("oid");
      logger.info("SMS code sent successfully, OID: {}", oid);
      return oid;
    } catch (EArsivException e) {
      logger.error("Failed to send SMS code: {}", e.getMessage());
      throw e;
    }
  }

  public JSONObject verifySignSMSCode(String oid, String smsCode, List<UUID> ids)
      throws EArsivException {
    List<JSONObject> dataList =
        ids.stream()
            .map(id -> new JSONObject().put("belgeTuru", "FATURA").put("ettn", id.toString()))
            .collect(Collectors.toList());

    JSONObject parameters =
        new JSONObject().put("OID", oid).put("SIFRE", smsCode).put("DATA", dataList).put("OPR", 1);

    return postDispatchRequest(createRequestBody("0lhozfib5410mp", "RG_SMSONAY", parameters));
  }
}
