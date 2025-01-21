package tr.bm.earsiv.enums;

public enum UnitType {
  PIECE("C62"), // Adet
  THOUSAND_PIECES("T3"), // Bin Adet
  DOZEN("DZN"), // Düzine
  PACKAGE("PA"), // Paket
  BOX("BX"), // Kutu
  SET("SET"), // Set
  PAIR("PR"), // Çift

  // Time units
  HOUR("HUR"), // Saat
  DAY("DAY"), // Gün
  MONTH("MON"), // Ay
  YEAR("ANN"), // Yıl
  MINUTE("D61"), // Dakika
  SECOND("D62"), // Saniye

  // Length units
  MILLIMETER("MMT"), // Milimetre
  CENTIMETER("CMT"), // Santimetre
  METER("MTR"), // Metre
  KILOMETER("KMT"), // Kilometre

  // Weight units
  MILLIGRAM("MGM"), // Miligram
  GRAM("GRM"), // Gram
  KILOGRAM("KGM"), // Kilogram
  TON("TNE"), // Ton
  NET_TON("NT"), // Net Ton
  GROSS_TON("GT"), // Brüt Ton
  CARAT("CT"), // Karat

  // Volume units
  MILLILITER("MLT"), // Mililitre
  CENTILITER("CLT"), // Santilitre
  LITER("LTR"), // Litre
  THOUSAND_LITERS("D40"), // Bin Litre
  PURE_ALCOHOL_LITER("LPA"), // Saf Alkol Litre
  CUBIC_MILLIMETER("MMQ"), // Milimetreküp
  CUBIC_CENTIMETER("CMQ"), // Santimetreküp
  CUBIC_METER("MTQ"), // Metreküp
  THOUSAND_CUBIC_METERS("R9"), // Bin Metreküp

  // Area units
  SQUARE_CENTIMETER("CMK"), // Santimetrekare
  SQUARE_METER("MTK"), // Metrekare
  SQUARE_DECIMETER("DMK"), // Desimetrekare
  KGM2("B32"), // KGM2

  // Energy units
  KILOJOULE("KJO"), // Kilojoule
  KILOWATT_HOUR("KWH"), // Kilowatt Saat
  MMBTU("J39"), // MMBTU
  GROSS_CALORIE("D30"), // Brüt Kalori

  // Special units
  CARRYING_CAPACITY_PER_TON("CCT"), // Ton Başına Taşıma Kapasitesi
  CELL_COUNT("NCL"), // Hücre Adedi
  SCM("Q37"), // SCM
  NCM("Q39"), // NCM
  CUBIC_METER_PER_DAY("G52"), // M3/Gün

  MEGAWATT_HOUR("MWH"), // Megawatt Saat
  DECIMETER("DMT"), // Desimetre
  HECTARE("HAR"), // Hektar
  LINEAR_METER("LM"); // Metretül

  private final String value;

  UnitType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
