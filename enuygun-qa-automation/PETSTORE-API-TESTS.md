# Petstore API Test Suite

Bu dokümantasyon, Petstore API için oluşturulan kapsamlı test suite'inin açıklamasını içerir.

## 📖 Genel Bakış

Petstore API test suite'i, Swagger Petstore API'sinin Pet endpoint'lerini test etmek için geliştirilmiş kapsamlı bir test framework'üdür. Java, REST Assured, TestNG ve ExtentReports kullanılarak oluşturulmuştur.

## 🏗️ Proje Yapısı

```
src/
├── main/java/com/enuygun/qa/
│   ├── api/
│   │   ├── base/
│   │   │   └── BaseApiTest.java          # API testleri için temel sınıf
│   │   ├── clients/
│   │   │   └── PetApiClient.java         # Pet API client wrapper
│   │   └── utils/
│   │       └── ApiUtils.java             # API test yardımcı metodları
│   └── models/petstore/
│       ├── Pet.java                      # Pet POJO modeli
│       ├── Category.java                 # Category POJO modeli
│       └── Tag.java                      # Tag POJO modeli
└── test/
    ├── java/com/enuygun/qa/api/tests/
    │   ├── PetApiTest.java               # Pozitif test senaryoları
    │   └── PetApiNegativeTest.java       # Negatif test senaryoları
    └── resources/
        ├── schemas/petstore/
        │   ├── pet-schema.json           # Pet response JSON schema
        │   ├── error-schema.json         # Error response JSON schema
        │   └── pet-array-schema.json     # Pet array response schema
        ├── testdata/petstore/
        │   ├── pet-test-data.json        # Test verileri
        │   └── invalid-pet-data.json     # Geçersiz test verileri
        └── testng-petstore-api.xml       # TestNG konfigürasyonu
```

## 🎯 Test Kapsamı

### CRUD Operations
- **CREATE (POST /pet)**: Yeni pet oluşturma
- **READ (GET /pet/{petId})**: Pet'i ID ile getirme
- **UPDATE (PUT /pet)**: Mevcut pet güncelleme
- **DELETE (DELETE /pet/{petId})**: Pet silme

### Arama Operations
- **GET /pet/findByStatus**: Status'a göre pet arama
- **GET /pet/findByTags**: Tag'lere göre pet arama

### Pozitif Test Senaryoları
1. **testCreatePet**: Geçerli verilerle pet oluşturma
2. **testGetPetById**: Oluşturulan pet'i ID ile getirme
3. **testUpdatePet**: Mevcut pet'i güncelleme
4. **testFindPetsByStatusAvailable**: Available status'taki pet'leri bulma
5. **testFindPetsByMultipleStatuses**: Birden fazla status ile arama
6. **testFindPetsByTags**: Tag'lere göre arama
7. **testCreatePetMinimalData**: Minimum gerekli verilerle pet oluşturma
8. **testCreateDifferentPetTypes**: Farklı pet türleri oluşturma
9. **testResponseHeaders**: Response header'ları doğrulama
10. **testDeletePet**: Pet silme

### Negatif Test Senaryoları
1. **testCreatePetWithInvalidJson**: Hatalı JSON ile pet oluşturma
2. **testCreatePetWithMissingRequiredFields**: Eksik zorunlu alanlarla test
3. **testCreatePetWithEmptyPhotoUrls**: Boş fotoğraf URL'leri ile test
4. **testCreatePetWithInvalidStatus**: Geçersiz status değeri ile test
5. **testGetNonExistentPet**: Olmayan pet'i getirme
6. **testGetPetWithInvalidIdFormat**: Geçersiz ID formatı ile test
7. **testUpdateNonExistentPet**: Olmayan pet'i güncelleme
8. **testUpdatePetWithInvalidData**: Geçersiz verilerle güncelleme
9. **testDeleteNonExistentPet**: Olmayan pet'i silme
10. **testFindPetsByInvalidStatus**: Geçersiz status ile arama
11. **testCreatePetWithLongName**: Çok uzun isimle pet oluşturma
12. **testCreatePetWithInvalidPhotoUrl**: Geçersiz foto URL'i ile test

## 🔧 Teknik Özellikler

### Framework & Libraries
- **Java 11+**: Programlama dili
- **REST Assured**: API test library
- **TestNG**: Test framework
- **Jackson**: JSON serialization/deserialization
- **NetworkNT JSON Schema Validator**: JSON schema validation
- **SLF4J + Logback**: Logging framework
- **ExtentReports**: Test reporting

### Design Patterns
- **Page Object Model (API Version)**: API client wrapper pattern
- **Builder Pattern**: POJO model oluşturma
- **Factory Pattern**: Static factory metodları
- **Singleton Pattern**: Configuration management

### Validation Features
- ✅ **Status Code Validation**: HTTP durum kodları kontrolü
- ✅ **Response Time Validation**: Yanıt süresi kontrolü
- ✅ **JSON Schema Validation**: Response yapısı doğrulama
- ✅ **Required Fields Validation**: Zorunlu alanların varlığı kontrolü
- ✅ **Data Integrity Validation**: Veri bütünlüğü kontrolü
- ✅ **Header Validation**: HTTP header'ları kontrolü
- ✅ **Error Message Validation**: Hata mesajları kontrolü

## 🚀 Kullanım

### Test Suite Çalıştırma

#### Tüm Testleri Çalıştırma
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-petstore-api.xml
```

#### Sadece Pozitif Testler
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-petstore-api.xml -Dtest="Pet API Positive Tests"
```

#### Sadece Negatif Testler
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-petstore-api.xml -Dtest="Pet API Negative Tests"
```

#### Smoke Testler
```bash
mvn test -DsuiteXmlFile=src/test/resources/testng-petstore-api.xml -Dtest="Pet API Smoke Tests"
```

### Maven Profiles

#### API Test Profili
```bash
mvn test -Papi-tests
```

#### Staging Environment
```bash
mvn test -Papi-tests -Denvironment=staging
```

#### Production Environment
```bash
mvn test -Papi-tests -Denvironment=production
```

## 📊 Raporlama

### ExtentReports
Testler çalıştıktan sonra aşağıdaki konumda detaylı HTML raporu oluşturulur:
```
target/extent-reports/api-test-report.html
```

### TestNG Reports
TestNG'nin kendi raporları:
```
target/surefire-reports/
```

### Loglar
Detaylı loglar:
```
logs/api-tests.log
```

## 🔧 Konfigürasyon

### Base URL Değiştirme
`src/main/java/com/enuygun/qa/config/ConfigManager.java` dosyasında:
```java
public static final String BASE_URL = "https://petstore.swagger.io/v2";
```

### Timeout Ayarları
`BaseApiTest.java` dosyasında:
```java
public static final long DEFAULT_RESPONSE_TIME_LIMIT = 5000; // 5 seconds
```

### Retry Ayarları
TestNG XML konfigürasyonunda:
```xml
<parameter name="retryCount" value="1"/>
```

## 📋 Test Verileri

### Geçerli Test Verileri
`src/test/resources/testdata/petstore/pet-test-data.json` dosyası:
- Çeşitli pet türleri (köpek, kedi, kuş, balık)
- Farklı kategoriler ve tag'ler
- Minimal ve tam veri setleri
- Güncelleme senaryoları için veri

### Geçersiz Test Verileri
`src/test/resources/testdata/petstore/invalid-pet-data.json` dosyası:
- Hatalı JSON yapıları
- Eksik zorunlu alanlar
- Geçersiz alan değerleri
- Ekstrem değerler
- Type validation hataları

## 🧪 JSON Schema Validation

### Pet Schema
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["id", "name", "photoUrls"],
  "properties": {
    "id": {"type": "integer", "minimum": 1},
    "name": {"type": "string", "minLength": 1},
    "photoUrls": {"type": "array", "minItems": 1},
    "status": {"enum": ["available", "pending", "sold"]}
  }
}
```

## 🔍 Best Practices

### Test Verisi Yönetimi
- Unique test verileri oluşturma (timestamp kullanımı)
- Test sonrası cleanup işlemleri
- Test verileri arasında dependency'den kaçınma

### Error Handling
- Comprehensive try-catch blokları
- Detaylı logging
- Graceful failure handling
- Retry mechanisms

### Performance Considerations
- Response time validations
- Parallel test execution
- Connection pooling
- Request/response size monitoring

## 🔧 Troubleshooting

### Yaygın Problemler

#### 1. Connection Timeout
```
Çözüm: timeout değerlerini artırın
<parameter name="timeout" value="15000"/>
```

#### 2. Schema Validation Errors
```
Çözüm: JSON schema dosyalarını kontrol edin
src/test/resources/schemas/petstore/
```

#### 3. Test Data Conflicts
```
Çözüm: Unique test data generation kullanın
ApiUtils.generateUniquePetName()
```

## 📈 Genişletme Rehberi

### Yeni Test Senaryoları Ekleme
1. `PetApiTest.java` veya `PetApiNegativeTest.java` dosyasına yeni test metodu ekleyin
2. TestNG XML konfigürasyonuna yeni test metodunu dahil edin
3. Gerekirse yeni test verileri ekleyin

### Yeni API Endpoint'leri
1. `BaseApiTest.java`'da yeni endpoint sabitleri tanımlayın
2. `PetApiClient.java`'da yeni API metotları ekleyin
3. Yeni test sınıfları oluşturun

### Yeni Validation Rules
1. `ApiUtils.java`'da yeni validation metotları ekleyin
2. JSON schema dosyalarını güncelleyin
3. Test metodlarında yeni validations kullanın

## 🤝 Katkıda Bulunma

1. Feature branch oluşturun
2. Testlerinizi yazın
3. Code review için PR açın
4. Test coverage'ını koruyun

## 📝 Notlar

- Testler staging environment'ta çalışacak şekilde konfigüre edilmiştir
- Production environment için ek güvenlik önlemleri gerekebilir
- API rate limiting'e karşı retry mechanism'ları eklenmiştir
- Comprehensive logging tüm API işlemleri için aktiftir

## 🔗 Referanslar

- [Swagger Petstore API Documentation](https://petstore.swagger.io/)
- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [JSON Schema Specification](https://json-schema.org/)