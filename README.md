# EArsiv

Bu proje, earsiv, efatura portalı ile yapılabilen basit fatura işlemleri için geliştirilmiş bir java kütüphanesidir. Geliştirilmeye devam edilmektedir ve kullanımında dikkatli olunmalıdır.

## ⚠️ ÖNEMLİ YASAL UYARI

**DİKKAT: Bu yazılım mali veri oluşturan ve vergisel yükümlülükler doğuran işlemler gerçekleştirmektedir!**

**SORUMLULUK REDDİ:**
- Bu yazılım "olduğu gibi" sunulmakta olup, kullanımından doğabilecek her türlü risk kullanıcıya aittir
- Yazılımın kullanımından kaynaklanan mali, hukuki ve vergisel sonuçlardan yazılım geliştiricileri ve dağıtıcıları hiçbir şekilde sorumlu tutulamaz
- Yazılımı kullanmadan önce tüm yasal yükümlülüklerinizi ve riskleri değerlendirmeniz önemle tavsiye edilir
- Herhangi bir risk almak istemiyorsanız bu yazılımı kullanmayınız


## Özellikler

- Basit fatura oluşturma
- Fatura iptal etme
- Fatura sorgulama
- Faturaları SMS ile onaylama
- Faturaları PDF olarak indirme

## Bağımlılıklar

Özellikle faturaları PDF'e doğru bir şekilde dönüştürmek için `Selenium` kütüphanesi kullanılmıştır. Bu sebeple projenin çalıştırılması için `ChromeDriver`'ın yüklü olması gerekmektedir.

## Kullanım

### Servisin oluşturulması

Prod modu için:

```java
EArsivService service = new EArsivService();
```

---
Test modu için:

```java
EArsivService service = new EArsivService(true);
```

### Kullanıcı işlemleri

Login işlemi:

```java
service.login("kullanıcı_adı", "parola");
```

---
Logout işlemi:

```java
service.logout();
```

### Fatura oluşturma

Fatura oluşturma işlemi basit vergi türlerini desteklemektedir. Daha detaylı vergi türleri için projeye katkıda bulunabilirsiniz.
Vergi hesaplaması griilen parametrelere göre otomatik olarak yapılmaktadır. Bu sebeple faturaları oluşturmadan önce doğruğunu mutlaka kontrol ediniz.

---
Satış işlemi için hızlı fatura oluşturma:

Verilen vergi no veya tckn'yi sistemden sorgular ve alıcı bilgilerini ekledikten sonra fatura oluşturur. Bu alıcı bilgileri istenirse parametrik olarak da verilebilir. Bu sorgulama işlemi test modunda çalışmamaktadır.

```java
UUID id = service.createQuickInvoiceForSales("vergi_no_yada_tckn", "Item Name", UnitType.PIECE, 1.0, 100.0, VatRate.TWENTY, 0.0);
```

---
Daha kapsamlı fatura oluşturma:

```java
InvoiceItem item = new InvoiceItem.Builder()
    .name("Test")
    .unitType(UnitType.PIECE)
    .quantity(1.0)
    .unitPrice(100)
    .vatRate(VatRate.TWENTY)
    .build();
Invoice invoice = new Invoice.Builder()
    .invoiceType(InvoiceType.SALES)
    .taxOrIdNumber("vergi_no_yada_tckn")
    .title("Ünvan")
    .name("Ad")
    .surname("Soyad")
    .addItem(item)
    .build();
service.createDraftInvoice(invoice);
```

### Fatura sorgulama

Zaman aralığında faturaları sorgulama:

```java
List<BasicInvoice> invoices = service.getInvoiceByDateRange(LocalDate.now().minusDays(1), LocalDate.now());
```

---
Gün ve id'ye göre fatura sorgulama:

```java
BasicInvoice invoice = service.getBasicInvoiceByDateAndId(LocalDate.now(), UUID.fromString("fatura_id"));
```

---
Fatura detayını alma

```java
JSONObject invoice = service.getInvoiceById(UUID.fromString("fatura_id"));
```

---
Fatura detayını HTML olarak alma

```java
String html = service.getInvoiceAsHtml(UUID.fromString("fatura_id"));
```

#### Fatura detayını PDF olarak alma

Fatura detayını PDF olarak alırken fatura detayının HTML olarak alınıp sonrasında PDF'e dönüştürülür. Bu PDF'e dönüştürme işlemi için `Selenium` kütüphanesi kullanılmıştır. Bu sebeple `ChromeDriver`'ın yüklü olması gerekmektedir.
PDF byte dizisi olarak döner ve bu byte dizisi dosyaya yazılabilir. Proje çalıştırıldığında ChromeDriver'ı çalıştırması biraz zaman alabilir fakat çalıştıktan sonra `HtmlToPdfConverter.shutdown()` metodu çağrılmadığı sürece ChromeDriver'ın çalışması devam eder ve PDF'e dönüştürme işlemleri hızlı bir şekilde gerçekleştirilir. Proje sonlanırken `HtmlToPdfConverter.shutdown()` metodu çağrılmalıdır.

PDF byte dizisi olarak alma:

```java
byte[] pdfBytes = service.getInvoiceAsPdf(UUID.fromString("fatura_id"));
```

---
PDF byte dizisini dosyaya yazma:

```java
Files.write(Paths.get("invoice.pdf"), pdfBytes);
```

### Fatura iptali

Fatura iptal işlemi topluca yapılabilir. Bunu içinde fautraların listesi ve iptal sebebi verilir. Fatura listesi `BasicInvoice` tipinde olmalıdır. Bu tipteki fatura detayını almak için `getBasicInvoiceByDateAndId` metodu yada `getInvoiceByDateRange` metodu kullanılabilir.

```java
service.cancelInvoice(Arrays.asList(invoice1, invoice2), "Hatalı işlem");
```

### SMS fatura imzalama

Lütfen dikkat! Bu aşamada faturalar imzalanmış olur ve mali değer taşır. Kullanırken dikkatli olunmalıdır.

Fatura imzalama işlemi için fatura id'lerinin listesi ve telefon numarası verilir. Telefon numarası verilmezse servis otomatik olarak sisteme kayıtlı telefon numarasını alır. Bu özellik test modunda çalışmamaktadır.

SMS kodu gönderimi:

Bu method çağrıldığında SMS kodu gönderilir ve geriye `oid` döner. Bu `oid` ile SMS kodunun doğrulanması gerekmektedir.

```java
String oid = service.sendSignSMSCode("telefon_numarası");
```

---
SMS kodu doğrulama:

Toplu doğrulama için `oid` ve SMS kodu verilir. Fatura id'lerinin listesi verilir. Bu liste içinde fatura id'leri `UUID` tipinde olmalıdır.

```java
service.verifySignSMSCode(oid, "sms_kodu", Arrays.asList(UUID.fromString("fatura_id_1"), UUID.fromString("fatura_id_2")));
```

## Build & Run

Projede bulunan `Main.java` dosyası içinde örnek bir kullanım bulunmaktadır. Bu dosyayı çalıştırmak için aşağıdaki adımları izleyebilirsiniz.

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/earsiv-0.0.1.jar
```

## Katkıda bulunma

Bu projeye katkıda bulunmak isteyenler için aşağıdaki adımları izleyebilirsiniz:

1. Bu repo'yu fork edin.
2. Yeni bir branch oluşturun.
3. Değişikliklerinizi yapın.
4. Değişikliklerinizi commit edin.
5. Pull request gönderin.
