package com.fenix.spirometer.ui.pcenter.detectorcalibration;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.room.DetectCompRepository;

import java.util.List;

public class DetectorCalibViewModel extends ViewModel {
    private final DetectCompRepository detectCompRepo;

    public DetectorCalibViewModel() {
        detectCompRepo = DetectCompRepository.getInstance();
    }

    public void subscribeToEstValues(LifecycleOwner lifecycleOwner, Observer<List<DetectorCompensation>> observer) {
        detectCompRepo.getAll().observe(lifecycleOwner, observer);
    }

    public void updateCompensation(DetectorCompensation compensation) {
        detectCompRepo.updateCompensation(compensation);
    }
}
