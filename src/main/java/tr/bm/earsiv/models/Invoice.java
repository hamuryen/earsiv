package tr.bm.earsiv.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.bm.earsiv.enums.Country;
import tr.bm.earsiv.enums.CurrencyType;
import tr.bm.earsiv.enums.InvoiceType;
import tr.bm.earsiv.exceptions.EArsivException;

public class Invoice {
  private static final Logger logger = LoggerFactory.getLogger(Invoice.class);

  private UUID uuid; // faturaUuid
  private String number; // belgeNumarasi
  private LocalDate date; // faturaTarihi
  private LocalTime time; // saat
  private CurrencyType currency; // paraBirimi
  private double currencyRate; // dovzTLkur (string)
  private InvoiceType invoiceType; // faturaTipi
  private String whichType; // hangiTip
  private String taxOrIdNumber; // vknTckn
  private String title; // aliciUnvan
  private String name; // aliciAdi
  private String surname; // aliciSoyadi
  private String buildingName; // binaAd
  private String buildingNumber; // binaNo
  private String doorNumber; // kapiNo
  private String town; // kasabaKoy
  private String taxOffice; // vergiDairesi
  private Country country; // ulke
  private String street; // bulvarcaddesokak
  private String dispatchNumber; // irsaliyeNumarasi
  private LocalDate dispatchDate; // irsaliyeTarihi
  private String districtNeighborhood; // mahalleSemtIlce
  private String city; // sehir
  private String postalCode; // postaKodu
  private String phone; // tel
  private String fax; // fax
  private String email; // eposta
  private String website; // websitesi
  private List<InvoiceItem> returns; // iadeTable
  private String taxType; // vergiCesidi
  private List<InvoiceItem> items; // malHizmetTable
  private String type; // tip

  private double baseAmount; // matrah (string)
  private double totalAmount; // malhizmetToplamTutari (string)
  private double totalDiscount; // toplamIskonto (string)
  private double totalVat; // hesaplanankdv (string)
  private double grandTotal; // vergilerDahilToplamTutar (string)
  private double payableAmount; // odenecekTutar (string)
  private double totalTaxes; // vergilerToplami (string)

  private String note; // not
  private String orderNumber; // siparisNumarasi
  private LocalDate orderDate; // siparisTarihi
  private String invoiceNumber; // fisNo
  private LocalDate invoiceDate; // fisTarihi
  private LocalTime invoiceTime; // fisSaati
  private String zReportNumber; // zRaporNo
  private String okcSerialNumber; // okcSeriNo

  private double vatRate; // kdvOrani
  private double vatAmount; // kdvTutari

  private Invoice(Builder builder) {
    this.uuid = builder.uuid;
    this.number = builder.number;
    this.date = builder.date;
    this.time = builder.time;
    this.currency = builder.currency;
    this.currencyRate = builder.currencyRate;
    this.invoiceType = builder.invoiceType;
    this.whichType = builder.whichType;
    this.taxOrIdNumber = builder.taxOrIdNumber;
    this.title = builder.title;
    this.name = builder.name;
    this.surname = builder.surname;
    this.buildingName = builder.buildingName;
    this.buildingNumber = builder.buildingNumber;
    this.doorNumber = builder.doorNumber;
    this.town = builder.town;
    this.taxOffice = builder.taxOffice;
    this.country = builder.country;
    this.street = builder.street;
    this.dispatchNumber = builder.dispatchNumber;
    this.dispatchDate = builder.dispatchDate;
    this.districtNeighborhood = builder.districtNeighborhood;
    this.city = builder.city;
    this.postalCode = builder.postalCode;
    this.phone = builder.phone;
    this.fax = builder.fax;
    this.email = builder.email;
    this.website = builder.website;
    this.returns = builder.returns;
    this.taxType = builder.taxType;
    this.items = builder.items;
    this.type = builder.type;
    this.baseAmount = builder.baseAmount;
    this.totalAmount = builder.totalAmount;
    this.totalDiscount = builder.totalDiscount;
    this.totalVat = builder.totalVat;
    this.grandTotal = builder.grandTotal;
    this.payableAmount = builder.payableAmount;
    this.totalTaxes = builder.totalTaxes;
    this.note = builder.note;
    this.orderNumber = builder.orderNumber;
    this.orderDate = builder.orderDate;
    this.invoiceNumber = builder.invoiceNumber;
    this.invoiceDate = builder.invoiceDate;
    this.invoiceTime = builder.invoiceTime;
    this.invoiceType = builder.invoiceType;
    this.zReportNumber = builder.zReportNumber;
    this.okcSerialNumber = builder.okcSerialNumber;
  }

  public static class Builder {
    private static final Logger logger = LoggerFactory.getLogger(Invoice.Builder.class);

    private UUID uuid = UUID.randomUUID();
    private String number = "";
    private LocalDate date = LocalDate.now();
    private LocalTime time = LocalTime.now();
    private CurrencyType currency = CurrencyType.TURK_LIRASI;
    private double currencyRate = 0.0;
    private InvoiceType invoiceType = InvoiceType.SALES;
    private String whichType = "5000/30000";
    private String taxOrIdNumber;
    private String title = "";
    private String name = "";
    private String surname = "";
    private String buildingName = "";
    private String buildingNumber = "";
    private String doorNumber = "";
    private String town = "";
    private String taxOffice = "";
    private Country country = Country.TURKIYE;
    private String street = "";
    private String dispatchNumber = "";
    private LocalDate dispatchDate = null;
    private String districtNeighborhood = "";
    private String city = "";
    private String postalCode = "";
    private String phone = "";
    private String fax = "";
    private String email = "";
    private String website = "";
    private List<InvoiceItem> returns = new ArrayList<>();
    private String taxType = "";
    private List<InvoiceItem> items = new ArrayList<>();
    private String type = "İskonto";
    private double baseAmount = 0.0;
    private double totalAmount = 0.0;
    private double totalDiscount = 0.0;
    private double totalVat = 0.0;
    private double grandTotal = 0.0;
    private double payableAmount = 0.0;
    private double totalTaxes = 0.0;
    private String note = "";
    private String orderNumber = "";
    private LocalDate orderDate = null;
    private String invoiceNumber = "";
    private LocalDate invoiceDate = null;
    private LocalTime invoiceTime = null;
    private String zReportNumber = "";
    private String okcSerialNumber = "";

    public Builder number(String number) {
      this.number = number;
      return this;
    }

    public Builder date(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder time(LocalTime time) {
      this.time = time;
      return this;
    }

    public Builder currency(CurrencyType currency) {
      this.currency = currency;
      return this;
    }

    public Builder currencyRate(double currencyRate) {
      this.currencyRate = currencyRate;
      return this;
    }

    public Builder invoiceType(InvoiceType invoiceType) {
      this.invoiceType = invoiceType;
      return this;
    }

    public Builder whichType(String whichType) {
      this.whichType = whichType;
      return this;
    }

    public Builder taxOrIdNumber(String taxOrIdNumber) {
      this.taxOrIdNumber = taxOrIdNumber;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder surname(String surname) {
      this.surname = surname;
      return this;
    }

    public Builder recipient(
        JSONObject recipient,
        String taxOrIdNumber,
        String taxOffice,
        String title,
        String name,
        String surname) {
      String to = recipient.getString("vergiDairesi");
      String t = recipient.getString("unvan");
      String n = recipient.getString("adi");
      String s = recipient.getString("soyadi");
      this.taxOrIdNumber = taxOrIdNumber;
      this.taxOffice = to != null && !to.isEmpty() ? to : taxOffice;
      this.title = t != null && !t.isEmpty() ? t : title;
      this.name = n != null && !n.isEmpty() ? n : name;
      this.surname = s != null && !s.isEmpty() ? s : surname;
      return this;
    }

    public Builder buildingName(String buildingName) {
      this.buildingName = buildingName;
      return this;
    }

    public Builder buildingNumber(String buildingNumber) {
      this.buildingNumber = buildingNumber;
      return this;
    }

    public Builder doorNumber(String doorNumber) {
      this.doorNumber = doorNumber;
      return this;
    }

    public Builder town(String town) {
      this.town = town;
      return this;
    }

    public Builder taxOffice(String taxOffice) {
      this.taxOffice = taxOffice;
      return this;
    }

    public Builder country(Country country) {
      this.country = country;
      return this;
    }

    public Builder street(String street) {
      this.street = street;
      return this;
    }

    public Builder dispatchNumber(String dispatchNumber) {
      this.dispatchNumber = dispatchNumber;
      return this;
    }

    public Builder dispatchDate(LocalDate dispatchDate) {
      this.dispatchDate = dispatchDate;
      return this;
    }

    public Builder districtNeighborhood(String districtNeighborhood) {
      this.districtNeighborhood = districtNeighborhood;
      return this;
    }

    public Builder city(String city) {
      this.city = city;
      return this;
    }

    public Builder postalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public Builder phone(String phone) {
      this.phone = phone;
      return this;
    }

    public Builder fax(String fax) {
      this.fax = fax;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder website(String website) {
      this.website = website;
      return this;
    }

    public Builder returns(List<InvoiceItem> returns) {
      this.returns = returns;
      return this;
    }

    public Builder taxType(String taxType) {
      this.taxType = taxType;
      return this;
    }

    public Builder addItem(InvoiceItem item) {
      this.items.add(item);
      return this;
    }

    public Builder items(List<InvoiceItem> items) {
      this.items = items;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder baseAmount(double baseAmount) {
      this.baseAmount = baseAmount;
      return this;
    }

    public Builder totalAmount(double totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Builder totalDiscount(double totalDiscount) {
      this.totalDiscount = totalDiscount;
      return this;
    }

    public Builder totalVat(double totalVat) {
      this.totalVat = totalVat;
      return this;
    }

    public Builder grandTotal(double grandTotal) {
      this.grandTotal = grandTotal;
      return this;
    }

    public Builder payableAmount(double payableAmount) {
      this.payableAmount = payableAmount;
      return this;
    }

    public Builder totalTaxes(double totalTaxes) {
      this.totalTaxes = totalTaxes;
      return this;
    }

    public Builder note(String note) {
      this.note = note;
      return this;
    }

    public Builder orderNumber(String orderNumber) {
      this.orderNumber = orderNumber;
      return this;
    }

    public Builder orderDate(LocalDate orderDate) {
      this.orderDate = orderDate;
      return this;
    }

    public Builder invoiceNumber(String invoiceNumber) {
      this.invoiceNumber = invoiceNumber;
      return this;
    }

    public Builder invoiceDate(LocalDate invoiceDate) {
      this.invoiceDate = invoiceDate;
      return this;
    }

    public Builder invoiceTime(LocalTime invoiceTime) {
      this.invoiceTime = invoiceTime;
      return this;
    }

    public Builder zReportNumber(String zReportNumber) {
      this.zReportNumber = zReportNumber;
      return this;
    }

    public Builder okcSerialNumber(String okcSerialNumber) {
      this.okcSerialNumber = okcSerialNumber;
      return this;
    }

    public Invoice build() throws EArsivException {
      logger.debug("Building invoice with tax number: {}", taxOrIdNumber);

      if (taxOrIdNumber == null || taxOrIdNumber.isEmpty()) {
        logger.error("VKN/TCKN is empty");
        throw new EArsivException("-1", "VKN/TCKN cannot be empty");
      }
      if ((title == null || title.isEmpty())
          && ((name == null || name.isEmpty()) || (surname == null || surname.isEmpty()))) {
        logger.error(
            "Invalid recipient information. Title: {}, Name: {}, Surname: {}",
            title,
            name,
            surname);
        throw new EArsivException(
            "-1", "Either title must be filled or both name and surname must be filled");
      }
      if (items == null || items.isEmpty()) {
        logger.error("No items in invoice");
        throw new EArsivException("-1", "Items cannot be empty");
      }

      Invoice invoice = new Invoice(this);
      invoice.calculate();

      logger.info("Invoice built successfully with {} items", items.size());
      return invoice;
    }
  }

  public Invoice calculate() throws EArsivException {
    logger.debug("Starting invoice calculation for invoice number: {}", this.number);

    double baseAmount = 0.0;
    double totalAmount = 0.0;
    double totalDiscount = 0.0;
    double totalVat = 0.0;
    double totalTaxes = 0.0;
    double grandTotal = 0.0;
    double vatRate = 0.0;
    double vatAmount = 0.0;

    logger.debug("Processing {} items", items.size());
    for (InvoiceItem item : items) {
      item.calculate();
      baseAmount += item.getTotalAmount();
      totalAmount += item.getPrice();
      totalDiscount += item.getDiscountAmount();
      totalVat += item.getVatAmount();
      totalTaxes += item.getTaxes();
      grandTotal += item.getGrandTotal();
      vatRate += item.getVatRate().getValue();
      vatAmount += item.getVatAmount();
    }

    this.baseAmount = baseAmount;
    this.totalAmount = totalAmount;
    this.totalDiscount = totalDiscount;
    this.totalVat = totalVat;
    this.totalTaxes = totalTaxes;
    this.grandTotal = grandTotal;
    this.payableAmount = grandTotal;
    this.vatRate = vatRate / (double) items.size();
    this.vatAmount = vatAmount;

    logger.info(
        "Invoice calculation completed. Base Amount: {}, Total: {}, VAT: {}, Grand Total: {}",
        baseAmount,
        totalAmount,
        totalVat,
        grandTotal);

    return this;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  // Getter methods for all fields
  public UUID getUuid() {
    return uuid;
  }

  public String getNumber() {
    return number;
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalTime getTime() {
    return time;
  }

  public CurrencyType getCurrency() {
    return currency;
  }

  public double getCurrencyRate() {
    return currencyRate;
  }

  public InvoiceType getInvoiceType() {
    return invoiceType;
  }

  public String getWhichType() {
    return whichType;
  }

  public String getTaxOrIdNumber() {
    return taxOrIdNumber;
  }

  public String getTitle() {
    return title;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public String getBuildingName() {
    return buildingName;
  }

  public String getBuildingNumber() {
    return buildingNumber;
  }

  public String getDoorNumber() {
    return doorNumber;
  }

  public String getTown() {
    return town;
  }

  public String getTaxOffice() {
    return taxOffice;
  }

  public Country getCountry() {
    return country;
  }

  public String getStreet() {
    return street;
  }

  public String getDispatchNumber() {
    return dispatchNumber;
  }

  public LocalDate getDispatchDate() {
    return dispatchDate;
  }

  public String getDistrictNeighborhood() {
    return districtNeighborhood;
  }

  public String getCity() {
    return city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getPhone() {
    return phone;
  }

  public String getFax() {
    return fax;
  }

  public String getEmail() {
    return email;
  }

  public String getWebsite() {
    return website;
  }

  public List<InvoiceItem> getReturns() {
    return returns;
  }

  public String getTaxType() {
    return taxType;
  }

  public List<InvoiceItem> getItems() {
    return items;
  }

  public String getType() {
    return type;
  }

  public double getBaseAmount() {
    return baseAmount;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public double getTotalDiscount() {
    return totalDiscount;
  }

  public double getTotalVat() {
    return totalVat;
  }

  public double getGrandTotal() {
    return grandTotal;
  }

  public double getPayableAmount() {
    return payableAmount;
  }

  public double getTotalTaxes() {
    return totalTaxes;
  }

  public String getNote() {
    return note;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public LocalDate getOrderDate() {
    return orderDate;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public LocalDate getInvoiceDate() {
    return invoiceDate;
  }

  public LocalTime getInvoiceTime() {
    return invoiceTime;
  }

  public String getZReportNumber() {
    return zReportNumber;
  }

  public String getOkcSerialNumber() {
    return okcSerialNumber;
  }

  public JSONObject toJsonObject() {
    logger.debug("Converting invoice to JSON. Invoice number: {}", this.number);

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    JSONObject json = new JSONObject();
    try {
      json.put("faturaUuid", uuid.toString());
      json.put("belgeNumarasi", number);
      json.put("faturaTarihi", date != null ? date.format(dateFormatter) : "");
      json.put("saat", time.format(timeFormatter));
      json.put("paraBirimi", currency.getValue());
      json.put("dovzTLkur", formatNumber(currencyRate));
      json.put("faturaTipi", invoiceType.getValue());
      json.put("hangiTip", whichType);
      json.put("vknTckn", taxOrIdNumber);
      json.put("aliciUnvan", title);
      json.put("aliciAdi", name);
      json.put("aliciSoyadi", surname);
      json.put("binaAdi", buildingName);
      json.put("binaNo", buildingNumber);
      json.put("kapiNo", doorNumber);
      json.put("kasabaKoy", town);
      json.put("vergiDairesi", taxOffice);
      json.put("ulke", country.getName());
      json.put("bulvarcaddesokak", street);
      json.put("irsaliyeNumarasi", dispatchNumber);
      json.put("irsaliyeTarihi", dispatchDate != null ? dispatchDate.format(dateFormatter) : "");
      json.put("mahalleSemtIlce", districtNeighborhood);
      json.put("sehir", city);
      json.put("postaKodu", postalCode != null ? postalCode : "");
      json.put("tel", phone != null ? phone : "");
      json.put("fax", fax != null ? fax : "");
      json.put("eposta", email != null ? email : "");
      json.put("websitesi", website != null ? website : "");

      logger.debug("Processing {} return items", returns.size());
      JSONArray returnsArray = new JSONArray();
      for (InvoiceItem item : returns) {
        returnsArray.put(item.toJsonObject());
      }
      json.put("iadeTable", returnsArray);

      logger.debug("Processing {} invoice items", items.size());
      JSONArray itemsArray = new JSONArray();
      for (InvoiceItem item : items) {
        itemsArray.put(item.toJsonObject());
      }
      json.put("malHizmetTable", itemsArray);

      json.put("vergiCesidi", taxType);
      json.put("iskontoNedeni", "");
      json.put("tip", type);
      json.put("matrah", formatNumber(baseAmount));
      json.put("malhizmetToplamTutari", formatNumber(totalAmount));
      json.put("toplamIskonto", formatNumber(totalDiscount));
      json.put("hesaplanankdv", formatNumber(vatRate));
      json.put("vergilerDahilToplamTutar", formatNumber(grandTotal));
      json.put("odenecekTutar", formatNumber(payableAmount));
      json.put("not", note);
      json.put("siparisNumarasi", orderNumber);
      json.put("siparisTarihi", orderDate != null ? orderDate.format(dateFormatter) : "");
      json.put("fisNo", invoiceNumber);
      json.put("fisTarihi", invoiceDate != null ? invoiceDate.format(dateFormatter) : "");
      json.put("fisSaati", invoiceTime != null ? invoiceTime.format(timeFormatter) : "");
      json.put("fisTipi", "");
      json.put("zRaporNo", zReportNumber);
      json.put("okcSeriNo", okcSerialNumber);
      json.put("ihracKayitliKarsiBelgeNo", "");
      json.put("kdvOrani", formatNumber(vatRate));
      json.put("kdvTutari", formatNumber(totalVat));
      json.put("vergilerToplami", formatNumber(totalTaxes));

      logger.trace("JSON conversion completed successfully");
      return json;
    } catch (Exception e) {
      logger.error("Error converting invoice to JSON: {}", e.getMessage(), e);
      throw e;
    }
  }

  private String formatNumber(double number) {
    String formatted = String.format("%.2f", number);
    if (formatted.endsWith(".00")) {
      return formatted.substring(0, formatted.length() - 3);
    }
    if (formatted.endsWith("0")) {
      return formatted.substring(0, formatted.length() - 1);
    }
    return formatted;
  }
}
