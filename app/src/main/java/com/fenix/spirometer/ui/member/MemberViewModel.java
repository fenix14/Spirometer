package com.fenix.spirometer.ui.member;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.room.AddrRepository;
import com.fenix.spirometer.model.City;
import com.fenix.spirometer.model.County;
import com.fenix.spirometer.model.Province;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemberViewModel extends ViewModel {
    private final ExecutorService cachedThreadPool;
    private final AddrRepository addrRepo;
    private MutableLiveData<List<Province>> mdProvinces;
    private MutableLiveData<List<City>> mdCities;
    private MutableLiveData<List<County>> mdCounties;

    public MemberViewModel() {
        addrRepo = AddrRepository.getInstance();
        cachedThreadPool = Executors.newCachedThreadPool();
    }
}
