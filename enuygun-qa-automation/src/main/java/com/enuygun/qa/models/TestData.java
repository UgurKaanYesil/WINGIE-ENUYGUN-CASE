package com.enuygun.qa.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestData {
    
    @JsonProperty("test_name")
    private String testName;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("expected_result")
    private String expectedResult;
    
    @JsonProperty("test_category")
    private String testCategory;
    
    @JsonProperty("priority")
    private int priority;

    public TestData() {
    }

    public TestData(String testName, String description, String expectedResult) {
        this.testName = testName;
        this.description = description;
        this.expectedResult = expectedResult;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "TestData{" +
                "testName='" + testName + '\'' +
                ", description='" + description + '\'' +
                ", expectedResult='" + expectedResult + '\'' +
                ", testCategory='" + testCategory + '\'' +
                ", priority=" + priority +
                '}';
    }
}