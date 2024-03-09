package com.example.demo.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.custom_data.CustomData;

public class ActivityViewModel extends ViewModel {

    private MutableLiveData<CustomData> customData = new MutableLiveData<>();

    public void setCustomData(CustomData data) {
        customData.setValue(data);
    }

    public MutableLiveData<CustomData> getCustomData() {
        return customData;
    }
}
