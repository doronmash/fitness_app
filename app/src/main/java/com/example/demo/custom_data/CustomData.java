package com.example.demo.custom_data;

import java.util.HashMap;
import java.util.Map;

public class CustomData {
    private HashMap<String, String> dataMap;

    public CustomData(HashMap<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    public HashMap<String, String> getData() {
        return dataMap;
    }

    public void addToDataMap(String key, String value) {
        dataMap.put(key, value);
    }
}
