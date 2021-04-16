package com.fenix.spirometer.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.ble.BleRepository;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.model.LoginState;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.model.TestInfo;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.room.AddrRepository;
import com.fenix.spirometer.room.EstValueRepository;
import com.fenix.spirometer.room.MemberRepository;
import com.fenix.spirometer.room.OperatorRepository;
import com.fenix.spirometer.room.DetectCompRepository;
import com.fenix.spirometer.room.TestReportRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private final MemberRepository memberRepo;
    private final AddrRepository addrRepo;
    private final OperatorRepository operRepo;
    private final DetectCompRepository detectCompRepo;
    private final EstValueRepository estValueRepo;
    private final TestReportRepository reportRepo;
    private BleRepository bleRepo;
    private MutableLiveData<TestInfo> mdTestInfo;

    private boolean isTesting = false;
    private Member chosenMember = null;

    private final MutableLiveData<Boolean> mdIsShowNavBar = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mdIsShowLightToolbar = new MutableLiveData<>(false);

    public MainViewModel() {
        memberRepo = MemberRepository.getInstance();
        addrRepo = AddrRepository.getInstance();
        operRepo = OperatorRepository.getInstance();
        detectCompRepo = DetectCompRepository.getInstance();
        estValueRepo = EstValueRepository.getInstance();
        bleRepo = BleRepository.getInstance();
        reportRepo = TestReportRepository.getInstance();
    }

    public void login(String userId, String password) {
        operRepo.login(userId, password);
    }

    public void changePassword(@NonNull String oldPassword,@NonNull String newPassword) {
        operRepo.changePassword(oldPassword, newPassword);
    }

    public void subscribeToLoginState(LifecycleOwner lifecycleOwner, Observer<LoginState> observer) {
        operRepo.getLoginState().observe(lifecycleOwner, observer);
    }
    public MutableLiveData<LoginState> getLoginState() {
        return operRepo.getLoginState();
    }

    public boolean isLogin() {
        LoginState loginState = operRepo.getLoginState().getValue();
        return loginState != null && loginState.isLogin();
    }

    public void logout() {
        operRepo.logout();
    }

    // 蓝牙设备相关
    public void connectToBleDevice(String mac) {
        bleRepo.connectTo(mac);
    }

    // 蓝牙设备相关
    public void disconnect() {
        bleRepo.disConnect();
    }

    public void subscribeToBleDeviceState(LifecycleOwner lifecycleOwner, Observer<BleDeviceState> observer) {
        bleRepo.getBleDeviceState().observe(lifecycleOwner, observer);
    }

    public MutableLiveData<BleDeviceState> getBleDeviceState() {
        return bleRepo.getBleDeviceState();
    }

    // 人员列表相关 TODO:有必要放这吗？
    public LiveData<List<Member>> getAllMembers() {
       return memberRepo.getAllMembers();
    }

    public LiveData<Member> getMember(String id) {
        return memberRepo.getMember(id);
    }

    public void insertOrUpdateMember(Member member) {
        chosenMember = member;
        memberRepo.insertOrUpdate(member);
    }

    public void updateMember(Member member) {
        cachedThreadPool.execute(() -> memberRepo.updateMember(member));
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    // for OperatorDetailFragment
    public void addOperator(Operator newOperator) {
        operRepo.insertOperator(newOperator);
    }

    public MutableLiveData<Operator> getOperator(String userId) {
        return operRepo.getOperator(userId);
    }

    // for HistoryFragment
    public LiveData<List<SimpleReport>> getAllSimpleReports() {
        return reportRepo.getSimpleReports();
    }

    // for HistoryFragment
    public LiveData<List<TestReport>> getAllReports() {
        return reportRepo.getReports(null);
    }

    // 底部Tab导航栏是否显示
    public void setShowNavBar(boolean isShowNavBar) {
        mdIsShowNavBar.postValue(isShowNavBar);
    }

    public void setShowLightToolbar(boolean isLightType) {
        if (isLightType != mdIsShowLightToolbar.getValue()) {
            mdIsShowLightToolbar.postValue(isLightType);
        }
    }

    public void subscribeToIsShowNavBar(LifecycleOwner lifecycleOwner, Observer<Boolean> observer) {
        mdIsShowNavBar.observe(lifecycleOwner, observer);
    }
    public void subscribeToIsLightToolbar(LifecycleOwner lifecycleOwner, Observer<Boolean> observer) {
        mdIsShowLightToolbar.observe(lifecycleOwner, observer);
    }

    public void setChosenMember(Member member) {
        chosenMember = member;
    }

    public Member getChosenMember() {
        return chosenMember;
    }
}
