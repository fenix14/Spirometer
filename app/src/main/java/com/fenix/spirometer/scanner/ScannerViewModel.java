package com.fenix.spirometer.scanner;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.BleDeviceState;

public class ScannerViewModel extends ViewModel {
    private MutableLiveData<BleDeviceState> mdBleDeviceState = new MutableLiveData<>();
    public void setBleDeviceState(BleDeviceState bleState) {
        mdBleDeviceState.postValue(bleState);
    }

    public void subscribeToBleDeviceState(LifecycleOwner lifecycleOwner, Observer<BleDeviceState> observer) {
        mdBleDeviceState.observe(lifecycleOwner, observer);
    }
}
