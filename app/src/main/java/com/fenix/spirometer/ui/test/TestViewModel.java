package com.fenix.spirometer.ui.test;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.ble.BleRepository;
import com.fenix.spirometer.ble.MeasureData;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.room.MemberRepository;

public class TestViewModel extends ViewModel {
    private final BleRepository bleRepo;
    private final MemberRepository memRepo;

    public TestViewModel() {
        bleRepo = BleRepository.getInstance();
        memRepo = MemberRepository.getInstance();
    }

    public MutableLiveData<BleDeviceState> getBleDeviceState() {
        return bleRepo.getBleDeviceState();
    }

    public MutableLiveData<MeasureData> getMeasureData() {
        return bleRepo.getMeasureData();
    }

    public void startMeasure() {
        bleRepo.startMeasure();
    }
    public void stopMeasure() {
        bleRepo.stopMeasure();
    }
}
