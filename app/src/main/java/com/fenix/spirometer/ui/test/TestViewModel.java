package com.fenix.spirometer.ui.test;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.ble.BleRepository;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.room.MemberRepository;

public class TestViewModel extends ViewModel {
    private final BleRepository bleRepo;
    private final MemberRepository memRepo;

    public TestViewModel() {
        bleRepo = BleRepository.getInstance();
        memRepo = MemberRepository.getInstance();
    }

    public void subscribeToBleDeviceState(LifecycleOwner lifecycleOwner, Observer<BleDeviceState> observer) {
        bleRepo.getBleDeviceState().observe(lifecycleOwner, observer);
    }
}
