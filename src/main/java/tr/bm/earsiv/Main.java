package tr.bm.earsiv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import tr.bm.earsiv.enums.UnitType;
import tr.bm.earsiv.enums.VatRate;
import tr.bm.earsiv.exceptions.EArsivException;
import tr.bm.earsiv.services.EArsivService;
import tr.bm.earsiv.utilities.HtmlToPdfConverter;

public class Main {
  public static void main(String[] args) {
    try {
      EArsivService service = new EArsivService(true);
      service.login("33333308", "1");
      String taxOrIdNumber = "11111111111";

      UUID id1 =
          service.createQuickInvoiceForSales(
              taxOrIdNumber, "Test 1", UnitType.PIECE, 1.0, 100.0, VatRate.TWENTY, 0.0);
      System.out.println("id1: " + id1);

      UUID id2 =
          service.createQuickInvoiceForSales(
              taxOrIdNumber, "Test 2", UnitType.PIECE, 2.0, 100.0, VatRate.TWENTY, 10.0);
      System.out.println("id2: " + id2);

      byte[] pdfBytes1 = service.getInvoiceAsPdf(id1);
      byte[] pdfBytes2 = service.getInvoiceAsPdf(id2);

      Files.write(Paths.get(id1.toString() + ".pdf"), pdfBytes1);
      Files.write(Paths.get(id2.toString() + ".pdf"), pdfBytes2);

      service.logout();

    } catch (EArsivException e) {
      System.err.println("Error code: " + e.getCode());
      System.err.println("Error message: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("An error occurred while writing the PDF file: " + e.getMessage());
      e.printStackTrace();
    } finally {
      HtmlToPdfConverter.shutdown();
    }
  }
}
