package tr.bm.earsiv.models;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.bm.earsiv.enums.UnitType;
import tr.bm.earsiv.enums.VatRate;
import tr.bm.earsiv.exceptions.EArsivException;

public class InvoiceItem {
  private static final Logger logger = LoggerFactory.getLogger(InvoiceItem.class);

  private String name; // MalHizmet
  private double quantity; // Miktar
  private UnitType unitType; // Birim (String)
  private double unitPrice; // Birim Fiyat (String)
  private double price; // Fiyat (String)
  private double discountRate; // İskonto Oranı
  private double discountAmount; // İskonto Tutarı (String)
  private String discountReason; // İskonto Nedeni
  private double totalAmount; // MalHizmet Tutarı (String)
  private VatRate vatRate; // KDV Oranı (String)
  private double taxRate; // Vergi Oranı
  private double vatAmount; // KDV Tutarı (String)
  private double taxVatAmount; // Verginin KDV Tutarı (String)
  private double specialTaxAmount; // Özel Matrah Tutarı (String)
  private double calculatedOtvTevkifataKatkisi; // Hesaplanan OTV Tevkifata Katkısı (String)
  private double finalAmount; // Vergi sonrası item için son tutar

  private InvoiceItem(Builder builder) {
    this.name = builder.name;
    this.quantity = builder.quantity;
    this.unitType = builder.unitType;
    this.unitPrice = builder.unitPrice;
    this.price = builder.price;
    this.discountRate = builder.discountRate;
    this.discountAmount = builder.discountAmount;
    this.discountReason = builder.discountReason;
    this.totalAmount = builder.totalAmount;
    this.vatRate = builder.vatRate;
    this.taxRate = builder.taxRate;
    this.vatAmount = builder.vatAmount;
    this.taxVatAmount = builder.taxVatAmount;
    this.specialTaxAmount = builder.specialTaxAmount;
    this.calculatedOtvTevkifataKatkisi = builder.calculatedOtvTevkifataKatkisi;
  }

  public static class Builder {
    private String name;
    private double quantity;
    private UnitType unitType;
    private double unitPrice;
    private double price;
    private double discountRate = 0.0;
    private double discountAmount = 0.0;
    private String discountReason = "";
    private double totalAmount;
    private VatRate vatRate;
    private double taxRate = 0.0;
    private double vatAmount = 0.0;
    private double taxVatAmount = 0.0;
    private double specialTaxAmount = 0.0;
    private double calculatedOtvTevkifataKatkisi = 0.0;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder quantity(double quantity) {
      this.quantity = quantity;
      return this;
    }

    public Builder unitType(UnitType unitType) {
      this.unitType = unitType;
      return this;
    }

    public Builder unitPrice(double unitPrice) {
      this.unitPrice = unitPrice;
      return this;
    }

    public Builder price(double price) {
      this.price = price;
      return this;
    }

    public Builder discountRate(double discountRate) {
      this.discountRate = discountRate;
      return this;
    }

    public Builder discountAmount(double discountAmount) {
      this.discountAmount = discountAmount;
      return this;
    }

    public Builder discountReason(String discountReason) {
      this.discountReason = discountReason;
      return this;
    }

    public Builder totalAmount(double totalAmount) {
      this.totalAmount = totalAmount;
      return this;
    }

    public Builder vatRate(VatRate vatRate) {
      this.vatRate = vatRate;
      return this;
    }

    public Builder taxRate(double taxRate) {
      this.taxRate = taxRate;
      return this;
    }

    public Builder vatAmount(double vatAmount) {
      this.vatAmount = vatAmount;
      return this;
    }

    public Builder taxVatAmount(double taxVatAmount) {
      this.taxVatAmount = taxVatAmount;
      return this;
    }

    public Builder specialTaxAmount(double specialTaxAmount) {
      this.specialTaxAmount = specialTaxAmount;
      return this;
    }

    public Builder calculatedOtvTevkifataKatkisi(double calculatedOtvTevkifataKatkisi) {
      this.calculatedOtvTevkifataKatkisi = calculatedOtvTevkifataKatkisi;
      return this;
    }

    public InvoiceItem build() {
      return new InvoiceItem(this);
    }
  }

  public InvoiceItem calculate() throws EArsivException {
    logger.debug("Calculating invoice item values for item: {}", this.name);

    if (this.quantity == 0 || this.unitPrice == 0) {
      logger.error(
          "Invalid quantity or unit price. Quantity: {}, Unit Price: {}",
          this.quantity,
          this.unitPrice);
      throw new EArsivException("-1", "Quantity and unit price cannot be zero");
    }

    this.price = this.quantity * this.unitPrice;
    logger.debug(
        "Calculated price: {} (quantity: {} * unitPrice: {})",
        this.price,
        this.quantity,
        this.unitPrice);

    this.discountAmount = (this.price * (double) this.discountRate) / 100;
    logger.debug(
        "Calculated discount amount: {} (price: {} * discountRate: {}%)",
        this.discountAmount, this.price, this.discountRate);

    this.totalAmount = this.price - this.discountAmount;
    this.vatAmount = (this.totalAmount * (double) this.vatRate.getValue()) / 100;
    this.finalAmount = this.totalAmount + this.vatAmount;

    logger.info(
        "Item calculation completed. Total: {}, VAT: {}, Final Amount: {}",
        this.totalAmount,
        this.vatAmount,
        this.finalAmount);

    return this;
  }

  public double getTaxes() {
    return this.vatAmount
        + this.taxVatAmount
        + this.specialTaxAmount
        + this.calculatedOtvTevkifataKatkisi;
  }

  public double getGrandTotal() {
    return this.totalAmount + this.getTaxes();
  }

  // Getter methods
  public String getName() {
    return name;
  }

  public double getQuantity() {
    return quantity;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public double getPrice() {
    return price;
  }

  public double getDiscountRate() {
    return discountRate;
  }

  public double getDiscountAmount() {
    return discountAmount;
  }

  public String getDiscountReason() {
    return discountReason;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public VatRate getVatRate() {
    return vatRate;
  }

  public double getTaxRate() {
    return taxRate;
  }

  public double getVatAmount() {
    return vatAmount;
  }

  public double getTaxVatAmount() {
    return taxVatAmount;
  }

  public double getSpecialTaxAmount() {
    return specialTaxAmount;
  }

  public double getCalculatedOtvTevkifataKatkisi() {
    return calculatedOtvTevkifataKatkisi;
  }

  public JSONObject toJsonObject() {
    logger.debug("Converting invoice item to JSON: {}", this.name);
    JSONObject json = new JSONObject();
    json.put("malHizmet", name);
    json.put("miktar", quantity);
    json.put("birim", unitType.getValue());
    json.put("birimFiyat", formatNumber(unitPrice));
    json.put("fiyat", formatNumber(price));
    json.put("iskontoOrani", discountRate);
    json.put("iskontoTutari", formatNumber(discountAmount));
    json.put("iskontoNedeni", discountReason);
    json.put("malHizmetTutari", formatNumber(totalAmount));
    json.put("kdvOrani", String.valueOf(vatRate.getValue()));
    json.put("vergiOrani", taxRate);
    json.put("kdvTutari", formatNumber(vatAmount));
    json.put("vergininKdvTutari", formatNumber(taxVatAmount));
    json.put("ozelMatrahTutari", formatNumber(specialTaxAmount));
    json.put("hesaplananotvtevkifatKatkisi", formatNumber(calculatedOtvTevkifataKatkisi));
    logger.trace("JSON conversion completed: {}", json.toString());
    return json;
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
