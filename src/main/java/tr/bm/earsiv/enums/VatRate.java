package tr.bm.earsiv.enums;

public enum VatRate {
  ZERO(0),
  ONE(1),
  EIGHT(8),
  TEN(10),
  TWENTY(20);

  private final int value;

  VatRate(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
