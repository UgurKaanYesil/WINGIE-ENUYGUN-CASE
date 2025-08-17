# Enuygun QA Automation Framework

Kapsamlı QA otomasyon framework'ü - UI, API, Load Testing ve Data Analysis için geliştirilmiş modern test altyapısı.

## 📖 Genel Bakış

Bu framework aşağıdaki test türlerini destekler:
- **UI Testing**: Selenium WebDriver tabanlı web otomasyon testleri
- **API Testing**: REST Assured ile API test framework'ü
- **Load Testing**: K6 ile performans ve yük testleri
- **Data Analysis**: Test sonuçları analizi ve raporlama

## 🏗️ Proje Yapısı

```
enuygun-qa-automation/
├── src/
│   ├── main/java/com/enuygun/qa/
│   │   ├── api/
│   │   │   ├── base/BaseApiTest.java          # API testleri temel sınıfı
│   │   │   ├── clients/PetApiClient.java      # Pet API client wrapper
│   │   │   └── utils/ApiUtils.java            # API yardımcı metodları
│   │   ├── config/
│   │   │   ├── ConfigManager.java             # Konfigürasyon yönetimi
│   │   │   └── TestConfig.java                # Test konfigürasyonları
│   │   ├── models/
│   │   │   ├── petstore/                      # Petstore API POJO modelleri
│   │   │   │   ├── Pet.java
│   │   │   │   ├── Category.java
│   │   │   │   └── Tag.java
│   │   │   ├── ApiResponse.java               # Genel API response modeli
│   │   │   └── TestData.java                  # Test verisi modeli
│   │   ├── pages/
│   │   │   ├── BasePage.java                  # Temel sayfa sınıfı
│   │   │   ├── HomePage.java                  # Ana sayfa page object
│   │   │   ├── SearchPage.java                # Arama sayfası
│   │   │   └── FlightListPage.java            # Uçuş listesi sayfası
│   │   └── utils/
│   │       ├── WebDriverFactory.java          # WebDriver yönetimi
│   │       ├── WaitUtils.java                 # Bekleme işlemleri
│   │       ├── ScreenshotUtils.java           # Ekran görüntüsü
│   │       └── ReportUtils.java               # Raporlama utilities
│   └── test/
│       ├── java/com/enuygun/qa/
│       │   ├── api/tests/                     # API test sınıfları
│       │   │   ├── PetApiTest.java            # Petstore pozitif testler
│       │   │   └── PetApiNegativeTest.java    # Petstore negatif testler
│       │   ├── ui/                            # UI test sınıfları
│       │   │   ├── HomePageTests.java         # Ana sayfa testleri
│       │   │   ├── SearchTests.java           # Arama testleri
│       │   │   └── FlightSearchBasicTest.java # Uçuş arama testleri
│       │   ├── data/
│       │   │   └── DataAnalysisTests.java     # Veri analizi testleri
│       │   └── base/
│       │       └── BaseTestClass.java         # Temel test sınıfı
│       └── resources/
│           ├── schemas/petstore/               # JSON schema dosyaları
│           │   ├── pet-schema.json
│           │   ├── error-schema.json
│           │   └── pet-array-schema.json
│           ├── testdata/petstore/              # Test verisi dosyaları
│           │   ├── pet-test-data.json
│           │   └── invalid-pet-data.json
│           ├── testng-petstore-api.xml        # Petstore API test suite
│           ├── testng-flight-search.xml       # Uçuş arama test suite
│           └── logback.xml                    # Logging konfigürasyonu
├── load-tests/k6-scripts/                     # K6 load test scriptleri
│   ├── load-test-flights.js
│   └── performance-test.js
├── test-data/                                 # Test verisi dosyaları
│   ├── flight-search-test-data.csv
│   └── test-datasets/
├── logs/                                      # Log dosyaları
├── screenshots/                               # Test sırasında alınan ekran görüntüleri
├── reports/                                   # Test raporları
├── PETSTORE-API-TESTS.md                     # Petstore API test dokümantasyonu
├── FLIGHT-SEARCH-TESTS.md                    # Uçuş arama test dokümantasyonu
└── pom.xml                                    # Maven konfigürasyonu
```

## 🛠️ Teknoloji Stack'i

### Core Framework
- **Java 11+**: Temel programlama dili
- **Maven**: Proje yönetimi ve build tool
- **TestNG**: Test framework

### UI Testing
- **Selenium WebDriver 4.15.0**: Web otomasyon
- **WebDriverManager 5.6.2**: Driver yönetimi

### API Testing
- **REST Assured 5.3.2**: API test library
- **Jackson 2.15.2**: JSON processing
- **NetworkNT JSON Schema Validator**: JSON schema validation

### Reporting & Logging
- **ExtentReports 5.1.1**: Test raporlama
- **SLF4J + Logback**: Logging framework

### Load Testing
- **K6**: Performans ve yük testleri

### Data Analysis
- **OpenCSV 5.8**: CSV işlemleri
- **JFreeChart 1.5.3**: Grafik oluşturma

## 🚀 Kurulum

### Ön Gereksinimler
- Java 11 veya üstü
- Maven 3.6+
- Chrome/Firefox browser

### Proje Kurulumu
```bash
git clone <repository-url>
cd enuygun-qa-automation
mvn clean compile
```

## 🧪 Test Çalıştırma

### 1. Petstore API Testleri

#### Tüm API Testlerini Çalıştırma
```bash
mvn test -Ppetstore-api-tests
```

#### Alternatif XML ile Çalıştırma
```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-petstore-api.xml
```

#### Sadece Pozitif Testler
```bash
mvn test -Dtest=PetApiTest
```

#### Sadece Negatif Testler
```bash
mvn test -Dtest=PetApiNegativeTest
```

### 2. UI Testleri

#### Uçuş Arama Testleri
```bash
mvn test -Pflight-search-tests
```

#### Browser Seçimi
```bash
mvn test -Pflight-search-tests -Dbrowser=chrome
mvn test -Pflight-search-tests -Dbrowser=firefox
```

### 3. Cross-Browser Testing
```bash
mvn test -Pcross-browser
```

### 4. Load Testing
```bash
cd load-tests/k6-scripts
k6 run load-test-flights.js
k6 run performance-test.js
```

## 📊 Test Sonuçları ve Raporlama

### ExtentReports
Test raporları otomatik olarak oluşturulur:
```
target/extent-reports/api-test-report.html
target/extent-reports/ui-test-report.html
```

### TestNG Reports
```
target/surefire-reports/
```

### Loglar
```
logs/automation.log
logs/test-results.log
```

### Screenshots
Başarısız testlerde otomatik ekran görüntüsü:
```
screenshots/
```

## 🎯 Test Kapsamı

### Petstore API Tests (22 Test)

#### Pozitif Senaryolar (10 Test)
- ✅ Pet oluşturma (POST /pet)
- ✅ Pet getirme (GET /pet/{petId})
- ✅ Pet güncelleme (PUT /pet)
- ✅ Pet silme (DELETE /pet/{petId})
- ✅ Status'a göre pet arama (GET /pet/findByStatus)
- ✅ Tag'lere göre pet arama (GET /pet/findByTags)
- ✅ Farklı pet türleri oluşturma
- ✅ Minimal veri ile pet oluşturma
- ✅ Response header validasyonu
- ✅ Birden fazla status ile arama

#### Negatif Senaryolar (12 Test)
- ❌ Geçersiz JSON ile pet oluşturma
- ❌ Eksik zorunlu alanlarla test
- ❌ Boş fotoğraf URL'leri
- ❌ Geçersiz status değeri
- ❌ Olmayan pet'i getirme (404 test)
- ❌ Geçersiz ID formatı
- ❌ Olmayan pet'i güncelleme
- ❌ Geçersiz veri ile güncelleme
- ❌ Olmayan pet'i silme
- ❌ Geçersiz status ile arama
- ❌ Çok uzun pet ismi
- ❌ Geçersiz foto URL formatı

### UI Tests
- Uçuş arama işlevselliği
- Form validasyonları
- Responsive design testleri
- Cross-browser uyumluluğu

### Load Tests
- Performans testleri
- Concurrent user simülasyonu
- Response time ölçümleri

## 🔧 Konfigürasyon

### Environment Ayarları
```bash
# Development
mvn test -Denvironment=dev

# Production
mvn test -Denvironment=prod
```

### Browser Konfigürasyonu
`src/main/java/com/enuygun/qa/config/ConfigManager.java`:
```java
public static final String DEFAULT_BROWSER = "chrome";
public static final int DEFAULT_TIMEOUT = 10;
```

### API Base URL
`src/main/java/com/enuygun/qa/api/base/BaseApiTest.java`:
```java
private static final String BASE_URL = "https://petstore.swagger.io/v2";
```

## 📋 Test Verileri

### API Test Data
- **Geçerli veriler**: `src/test/resources/testdata/petstore/pet-test-data.json`
- **Geçersiz veriler**: `src/test/resources/testdata/petstore/invalid-pet-data.json`

### JSON Schema Validation
- **Pet Schema**: `src/test/resources/schemas/petstore/pet-schema.json`
- **Error Schema**: `src/test/resources/schemas/petstore/error-schema.json`
- **Array Schema**: `src/test/resources/schemas/petstore/pet-array-schema.json`

### UI Test Data
- **CSV dosyası**: `test-data/flight-search-test-data.csv`
- **Properties**: `test-data/test-config.properties`

## 🎨 Design Patterns

### Page Object Model (UI)
```java
public class HomePage extends BasePage {
    @FindBy(id = "search-button")
    private WebElement searchButton;
    
    public SearchPage clickSearch() {
        click(searchButton);
        return new SearchPage(driver);
    }
}
```

### API Client Pattern
```java
public class PetApiClient extends BaseApiTest {
    public Response createPet(Pet pet) {
        return given()
            .spec(getRequestSpecification())
            .body(pet)
            .when()
            .post("/pet");
    }
}
```

### Builder Pattern (POJO)
```java
Pet pet = new Pet()
    .withName("Buddy")
    .withCategory(Category.createDog())
    .withPhotoUrl("http://example.com/photo.jpg")
    .withAvailableStatus();
```

## 🔍 Best Practices

### Test Data Management
- Unique test data generation
- Test cleanup after execution
- Environment-specific configurations

### Error Handling
- Comprehensive try-catch blocks
- Detailed logging
- Graceful failure handling
- Automatic retry mechanisms

### Performance
- Parallel test execution
- Connection pooling
- Response time validations
- Resource optimization

## 🛡️ Quality Assurance

### Code Quality
- SonarQube integration ready
- Checkstyle rules
- PMD static analysis
- JaCoCo code coverage

### Test Reliability
- Explicit waits
- Retry mechanisms
- Environment isolation
- Data-driven testing

## 📈 CI/CD Integration

### Jenkins Pipeline Ready
```groovy
pipeline {
    stages {
        stage('API Tests') {
            steps {
                sh 'mvn test -Ppetstore-api-tests'
            }
        }
        stage('UI Tests') {
            steps {
                sh 'mvn test -Pflight-search-tests'
            }
        }
    }
}
```

### GitHub Actions
```yaml
name: QA Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run API Tests
        run: mvn test -Ppetstore-api-tests
```

## 🔗 Yararlı Linkler

- [Petstore API Documentation](https://petstore.swagger.io/)
- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [Selenium Documentation](https://selenium.dev/)
- [K6 Documentation](https://k6.io/docs/)

## 📞 Destek

Sorularınız için:
- Issue açın: [GitHub Issues]
- Dokümantasyon: `PETSTORE-API-TESTS.md`, `FLIGHT-SEARCH-TESTS.md`
- Teknik detaylar: Framework kod dokümantasyonu

## 📝 Changelog

### v1.0.0 (2025-08-17)
- ✅ Petstore API test framework implementasyonu
- ✅ 22 comprehensive API test case
- ✅ JSON schema validation
- ✅ Maven profiles ve CI/CD hazırlığı
- ✅ Kapsamlı logging ve reporting
- ✅ Error handling ve retry mechanisms

### Future Enhancements
- [ ] Database testing integration
- [ ] Mobile testing support  
- [ ] Advanced reporting dashboard
- [ ] Test management tool integration
- [ ] Kubernetes test execution support

---

**Framework Version**: 1.0.0  
**Last Updated**: August 17, 2025  
**Maintainer**: QA Team