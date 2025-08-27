package com.example.myapplication3;

public class TestData {
    public String message;
    public int value;
    
    public TestData(String message, int value) {
        this.message = message;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "TestData{message='" + message + "', value=" + value + '}';
    }
}