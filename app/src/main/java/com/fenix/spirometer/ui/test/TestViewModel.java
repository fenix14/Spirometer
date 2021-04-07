package com.fenix.spirometer.ui.test;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.ble.BleRepository;
import com.fenix.spirometer.ble.MeasureData;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.room.MemberRepository;
import com.fenix.spirometer.room.TestReportRepository;

import java.util.List;

public class TestViewModel extends ViewModel {
    private final BleRepository bleRepo;
    private final MemberRepository memRepo;
    private final TestReportRepository reportRepo;

    public TestViewModel() {
        bleRepo = BleRepository.getInstance();
        memRepo = MemberRepository.getInstance();
        reportRepo = TestReportRepository.getInstance();
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

    public void insertReport(TestReport testReport) {
        reportRepo.insert(testReport);
    }

    public MutableLiveData<TestReport> getReport(long timeStamp) {
        return reportRepo.getReport(timeStamp);
    }

    public MutableLiveData<List<TestReport>> getReports(long[] timeStamps) {
        return reportRepo.getReports(timeStamps);
    }
}
