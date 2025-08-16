package com.enuygun.qa.data;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.enuygun.qa.base.BaseTestClass;
import com.enuygun.qa.config.ConfigManager;
import com.enuygun.qa.config.TestConfig;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DataAnalysisTests extends BaseTestClass {

    private String testDataDirectory;
    private String reportDirectory;

    @BeforeClass
    public void setupDataAnalysis() {
        testDataDirectory = ConfigManager.getTestDataDirectory();
        reportDirectory = ConfigManager.getReportsDirectory();
        
        // Create test data directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(testDataDirectory));
            Files.createDirectories(Paths.get(reportDirectory));
            logger.info("Data analysis setup completed");
            ReportUtils.logInfo("Data Analysis Test Setup - Directories verified");
        } catch (Exception e) {
            logger.error("Failed to setup data analysis directories", e);
            throw new RuntimeException("Failed to setup data analysis", e);
        }
    }

    @Test(groups = {TestConfig.DATA_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Generate sample flight booking data for analysis")
    public void testGenerateSampleData() {
        logTestStep("Generate sample flight booking data");
        
        String csvFilePath = testDataDirectory + "/flight_bookings_sample.csv";
        
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            // Write header
            String[] header = {"booking_id", "origin", "destination", "date", "price", "airline", "passenger_count", "booking_date"};
            writer.writeNext(header);
            
            // Generate sample data
            String[] origins = {"IST", "ESB", "ADB", "AYT", "TZX"};
            String[] destinations = {"LHR", "CDG", "AMS", "FCO", "BCN", "MAD", "MUC", "ZUR"};
            String[] airlines = {"Turkish Airlines", "Pegasus", "Onur Air", "SunExpress"};
            
            Random random = new Random();
            
            for (int i = 1; i <= 1000; i++) {
                String[] row = {
                    "BK" + String.format("%06d", i),
                    origins[random.nextInt(origins.length)],
                    destinations[random.nextInt(destinations.length)],
                    "2024-" + String.format("%02d", random.nextInt(12) + 1) + "-" + String.format("%02d", random.nextInt(28) + 1),
                    String.valueOf(500 + random.nextInt(2000)), // Price between 500-2500
                    airlines[random.nextInt(airlines.length)],
                    String.valueOf(random.nextInt(4) + 1), // 1-4 passengers
                    "2024-" + String.format("%02d", random.nextInt(6) + 1) + "-" + String.format("%02d", random.nextInt(28) + 1)
                };
                writer.writeNext(row);
            }
            
            logTestStep("Sample data generated successfully");
            assertAndLog(Files.exists(Paths.get(csvFilePath)), 
                        "Sample CSV file created: " + csvFilePath, 
                        "Failed to create sample CSV file");
            
            ReportUtils.logInfo("Generated 1000 sample flight booking records");
            
        } catch (Exception e) {
            logger.error("Failed to generate sample data", e);
            ReportUtils.logFail("Failed to generate sample data: " + e.getMessage());
            throw new RuntimeException("Failed to generate sample data", e);
        }
    }

    @Test(groups = {TestConfig.DATA_TEST}, 
          priority = TestConfig.MEDIUM_PRIORITY,
          description = "Analyze flight booking data and generate insights",
          dependsOnMethods = "testGenerateSampleData")
    public void testAnalyzeFlightBookingData() {
        logTestStep("Read and analyze flight booking data");
        
        String csvFilePath = testDataDirectory + "/flight_bookings_sample.csv";
        List<Map<String, String>> bookings = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] header = reader.readNext();
            String[] row;
            
            while ((row = reader.readNext()) != null) {
                Map<String, String> booking = new HashMap<>();
                for (int i = 0; i < header.length && i < row.length; i++) {
                    booking.put(header[i], row[i]);
                }
                bookings.add(booking);
            }
            
            logTestStep("Analyze booking patterns");
            
            // Analyze popular destinations
            Map<String, Long> destinationCounts = bookings.stream()
                    .collect(Collectors.groupingBy(b -> b.get("destination"), Collectors.counting()));
            
            logTestStep("Most popular destinations analysis");
            destinationCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .forEach(entry -> 
                        ReportUtils.logInfo("Destination: " + entry.getKey() + " - Bookings: " + entry.getValue()));
            
            // Analyze airline market share
            Map<String, Long> airlineCounts = bookings.stream()
                    .collect(Collectors.groupingBy(b -> b.get("airline"), Collectors.counting()));
            
            logTestStep("Airline market share analysis");
            airlineCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry -> 
                        ReportUtils.logInfo("Airline: " + entry.getKey() + " - Market Share: " + 
                                          String.format("%.1f%%", (entry.getValue() * 100.0) / bookings.size())));
            
            // Price analysis
            OptionalDouble avgPrice = bookings.stream()
                    .mapToDouble(b -> Double.parseDouble(b.get("price")))
                    .average();
            
            double minPrice = bookings.stream()
                    .mapToDouble(b -> Double.parseDouble(b.get("price")))
                    .min().orElse(0);
            
            double maxPrice = bookings.stream()
                    .mapToDouble(b -> Double.parseDouble(b.get("price")))
                    .max().orElse(0);
            
            logTestStep("Price analysis completed");
            ReportUtils.logInfo("Average Price: " + String.format("%.2f", avgPrice.orElse(0)));
            ReportUtils.logInfo("Min Price: " + String.format("%.2f", minPrice));
            ReportUtils.logInfo("Max Price: " + String.format("%.2f", maxPrice));
            
            assertAndLog(bookings.size() == 1000, 
                        "Successfully analyzed 1000 booking records", 
                        "Data analysis failed - incorrect record count: " + bookings.size());
            
        } catch (Exception e) {
            logger.error("Failed to analyze booking data", e);
            ReportUtils.logFail("Failed to analyze booking data: " + e.getMessage());
            throw new RuntimeException("Failed to analyze booking data", e);
        }
    }

    @Test(groups = {TestConfig.DATA_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Generate data visualization charts",
          dependsOnMethods = "testAnalyzeFlightBookingData")
    public void testGenerateDataVisualization() {
        logTestStep("Generate data visualization charts");
        
        String csvFilePath = testDataDirectory + "/flight_bookings_sample.csv";
        List<Map<String, String>> bookings = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] header = reader.readNext();
            String[] row;
            
            while ((row = reader.readNext()) != null) {
                Map<String, String> booking = new HashMap<>();
                for (int i = 0; i < header.length && i < row.length; i++) {
                    booking.put(header[i], row[i]);
                }
                bookings.add(booking);
            }
            
            // Generate destination popularity pie chart
            logTestStep("Generate destination popularity chart");
            generateDestinationChart(bookings);
            
            // Generate airline market share bar chart
            logTestStep("Generate airline market share chart");
            generateAirlineChart(bookings);
            
            // Generate price distribution chart
            logTestStep("Generate price distribution chart");
            generatePriceDistributionChart(bookings);
            
            ReportUtils.logInfo("Data visualization charts generated successfully");
            
        } catch (Exception e) {
            logger.error("Failed to generate data visualization", e);
            ReportUtils.logFail("Failed to generate data visualization: " + e.getMessage());
            throw new RuntimeException("Failed to generate data visualization", e);
        }
    }

    @Test(groups = {TestConfig.DATA_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Validate data quality and integrity")
    public void testDataQualityValidation() {
        logTestStep("Validate data quality and integrity");
        
        String csvFilePath = testDataDirectory + "/flight_bookings_sample.csv";
        List<String> validationErrors = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] header = reader.readNext();
            String[] row;
            int rowNumber = 1;
            
            while ((row = reader.readNext()) != null) {
                rowNumber++;
                
                // Validate booking ID format
                if (!row[0].matches("BK\\d{6}")) {
                    validationErrors.add("Row " + rowNumber + ": Invalid booking ID format: " + row[0]);
                }
                
                // Validate price is numeric and positive
                try {
                    double price = Double.parseDouble(row[4]);
                    if (price <= 0) {
                        validationErrors.add("Row " + rowNumber + ": Invalid price: " + price);
                    }
                } catch (NumberFormatException e) {
                    validationErrors.add("Row " + rowNumber + ": Non-numeric price: " + row[4]);
                }
                
                // Validate passenger count
                try {
                    int passengers = Integer.parseInt(row[6]);
                    if (passengers < 1 || passengers > 9) {
                        validationErrors.add("Row " + rowNumber + ": Invalid passenger count: " + passengers);
                    }
                } catch (NumberFormatException e) {
                    validationErrors.add("Row " + rowNumber + ": Non-numeric passenger count: " + row[6]);
                }
                
                // Validate date format (basic check)
                if (!row[3].matches("\\d{4}-\\d{2}-\\d{2}")) {
                    validationErrors.add("Row " + rowNumber + ": Invalid date format: " + row[3]);
                }
            }
            
            logTestStep("Data quality validation completed");
            
            if (validationErrors.isEmpty()) {
                ReportUtils.logPass("Data quality validation passed - no errors found");
            } else {
                ReportUtils.logInfo("Data quality issues found: " + validationErrors.size());
                validationErrors.stream().limit(10).forEach(ReportUtils::logInfo); // Log first 10 errors
            }
            
            assertAndLog(validationErrors.size() < 50, 
                        "Data quality is acceptable (errors: " + validationErrors.size() + ")", 
                        "Too many data quality issues found: " + validationErrors.size());
            
        } catch (Exception e) {
            logger.error("Failed to validate data quality", e);
            ReportUtils.logFail("Failed to validate data quality: " + e.getMessage());
            throw new RuntimeException("Failed to validate data quality", e);
        }
    }

    @Test(groups = {TestConfig.DATA_TEST}, 
          priority = TestConfig.LOW_PRIORITY,
          description = "Export analysis results to summary report")
    public void testExportAnalysisResults() {
        logTestStep("Export analysis results to summary report");
        
        String summaryFilePath = reportDirectory + "/data_analysis_summary.txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(summaryFilePath))) {
            writer.println("=== ENUYGUN QA AUTOMATION - DATA ANALYSIS SUMMARY ===");
            writer.println("Generated on: " + new Date());
            writer.println();
            
            writer.println("ANALYSIS OVERVIEW:");
            writer.println("- Sample data generated: 1000 flight booking records");
            writer.println("- Data quality validation completed");
            writer.println("- Visualization charts generated");
            writer.println();
            
            writer.println("KEY METRICS:");
            writer.println("- Total bookings analyzed: 1000");
            writer.println("- Unique destinations: 8");
            writer.println("- Airlines covered: 4");
            writer.println("- Price range: 500 - 2500");
            writer.println();
            
            writer.println("GENERATED ARTIFACTS:");
            writer.println("- flight_bookings_sample.csv");
            writer.println("- destination_popularity_chart.png");
            writer.println("- airline_market_share_chart.png");
            writer.println("- price_distribution_chart.png");
            writer.println();
            
            writer.println("=== END OF SUMMARY ===");
            
            logTestStep("Summary report exported successfully");
            assertAndLog(Files.exists(Paths.get(summaryFilePath)), 
                        "Analysis summary exported: " + summaryFilePath, 
                        "Failed to export analysis summary");
            
            ReportUtils.logInfo("Data analysis summary exported to: " + summaryFilePath);
            
        } catch (Exception e) {
            logger.error("Failed to export analysis results", e);
            ReportUtils.logFail("Failed to export analysis results: " + e.getMessage());
            throw new RuntimeException("Failed to export analysis results", e);
        }
    }

    private void generateDestinationChart(List<Map<String, String>> bookings) throws Exception {
        Map<String, Long> destinationCounts = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.get("destination"), Collectors.counting()));
        
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        destinationCounts.forEach((destination, count) -> dataset.setValue(destination, count));
        
        JFreeChart chart = ChartFactory.createPieChart(
                "Flight Destinations Popularity",
                dataset,
                true, true, false);
        
        String chartPath = reportDirectory + "/destination_popularity_chart.png";
        ChartUtils.saveChartAsPNG(new File(chartPath), chart, 800, 600);
        
        ReportUtils.logInfo("Destination chart saved: " + chartPath);
    }

    private void generateAirlineChart(List<Map<String, String>> bookings) throws Exception {
        Map<String, Long> airlineCounts = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.get("airline"), Collectors.counting()));
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        airlineCounts.forEach((airline, count) -> dataset.addValue(count, "Bookings", airline));
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Airline Market Share",
                "Airlines",
                "Number of Bookings",
                dataset);
        
        String chartPath = reportDirectory + "/airline_market_share_chart.png";
        ChartUtils.saveChartAsPNG(new File(chartPath), chart, 800, 600);
        
        ReportUtils.logInfo("Airline chart saved: " + chartPath);
    }

    private void generatePriceDistributionChart(List<Map<String, String>> bookings) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Group prices into ranges
        Map<String, Long> priceRanges = bookings.stream()
                .collect(Collectors.groupingBy(b -> {
                    double price = Double.parseDouble(b.get("price"));
                    if (price < 750) return "500-749";
                    else if (price < 1000) return "750-999";
                    else if (price < 1500) return "1000-1499";
                    else if (price < 2000) return "1500-1999";
                    else return "2000+";
                }, Collectors.counting()));
        
        priceRanges.forEach((range, count) -> dataset.addValue(count, "Bookings", range));
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Price Distribution",
                "Price Range",
                "Number of Bookings",
                dataset);
        
        String chartPath = reportDirectory + "/price_distribution_chart.png";
        ChartUtils.saveChartAsPNG(new File(chartPath), chart, 800, 600);
        
        ReportUtils.logInfo("Price distribution chart saved: " + chartPath);
    }
}