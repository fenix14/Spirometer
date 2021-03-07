package com.fenix.spirometer.ui.main;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.Administrator;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private MutableLiveData<BleDeviceState> mdBleDeviceState = new MutableLiveData<>();

    private MutableLiveData<Administrator> mdAdministrator = new MutableLiveData<>(new Administrator("18674018759", "HeFengfei", "123456", "Nothing"));

    private MutableLiveData<List<Member>> mdMembers = new MutableLiveData<>();

    private MutableLiveData<Integer> mdNavigationBarType = new MutableLiveData<>();

    private MutableLiveData<Integer> mdToolbarType = new MutableLiveData<>();

    public MainViewModel() {
        List<Member> members = new ArrayList<>();
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二三", "男", 18, 78, 142, "内蒙古", "呼和浩特"));
        members.add(new Member("一", "男", 18, 78, 142, "新疆", "佳木斯"));
        members.add(new Member("一二三四", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        members.add(new Member("一二", "男", 18, 78, 142, "湖北", "武汉"));
        mdMembers.postValue(members);
    }

    public void setAdministrator(Administrator admin) {
        mdAdministrator.postValue(admin);
    }

    public void subscribeToAdministrator(LifecycleOwner lifecycleOwner, Observer<Administrator> observer) {
        mdAdministrator.observe(lifecycleOwner, observer);
    }

    public void setBleDeviceState(BleDeviceState bleState) {
        mdBleDeviceState.postValue(bleState);
    }

    public void subscribeToBleDeviceState(LifecycleOwner lifecycleOwner, Observer<BleDeviceState> observer) {
        mdBleDeviceState.observe(lifecycleOwner, observer);
    }

    public void setMembers(List<Member> members) {
        mdMembers.postValue(members);
    }

    public void subscribeToMembers(LifecycleOwner lifecycleOwner, Observer<List<Member>> observer) {
        mdMembers.observe(lifecycleOwner, observer);
    }


    public void setNavigationBarBg(int color) {
        mdNavigationBarType.postValue(color);
    }

    public void subscribeToNavigationBarBg(LifecycleOwner lifecycleOwner, Observer<Integer> observer) {
        mdNavigationBarType.observe(lifecycleOwner, observer);
    }
    public void setToolbarType(@Constants.BgType int toolbarType) {
        mdToolbarType.postValue(toolbarType);
    }

    public void subscribeToToolbarType(LifecycleOwner lifecycleOwner, Observer<Integer> observer) {
        mdToolbarType.observe(lifecycleOwner, observer);
    }
}
