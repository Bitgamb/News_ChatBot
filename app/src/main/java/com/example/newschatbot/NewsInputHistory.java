package com.example.newschatbot;

public class NewsInputHistory {
    private String key;
    private String value;
    private int serialNumber;

    public NewsInputHistory() {
        // Default constructor required for Firebase
    }

    public NewsInputHistory(String key, String value, int serialNumber) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
    public int getSerialNumber() {
        return serialNumber;
    }
}
