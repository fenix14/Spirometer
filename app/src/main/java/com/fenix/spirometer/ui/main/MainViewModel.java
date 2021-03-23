package com.fenix.spirometer.ui.main;

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
import com.fenix.spirometer.model.TestInfo;
import com.fenix.spirometer.room.AddrRepository;
import com.fenix.spirometer.room.EstValueRepository;
import com.fenix.spirometer.room.MemberRepository;
import com.fenix.spirometer.room.OperatorRepository;
import com.fenix.spirometer.room.DetectCompRepository;

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
    private BleRepository bleRepo;
    private MutableLiveData<TestInfo> mdTestInfo;

    private boolean isTesting = false;
    private Member chosenMember = null;

    private final MutableLiveData<Boolean> mdIsShowNavBar = new MutableLiveData<>();

    public MainViewModel() {
        memberRepo = MemberRepository.getInstance();
        addrRepo = AddrRepository.getInstance();
        operRepo = OperatorRepository.getInstance();
        detectCompRepo = DetectCompRepository.getInstance();
        estValueRepo = EstValueRepository.getInstance();
        bleRepo = BleRepository.getInstance();
    }

    public void login(String userId, String password) {

        operRepo.login(userId, password);
    }

    public void subscribeToLoginState(LifecycleOwner lifecycleOwner, Observer<LoginState> observer) {
        operRepo.getLoginState().observe(lifecycleOwner, observer);
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

    public void subscribeToBleDeviceState(LifecycleOwner lifecycleOwner, Observer<BleDeviceState> observer) {
        bleRepo.getBleDeviceState().observe(lifecycleOwner, observer);
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

    // 底部Tab导航栏是否显示
    public void setShowNavBar(boolean isShowNavBar) {
        mdIsShowNavBar.postValue(isShowNavBar);
    }

    public void subscribeToIsShowNavBar(LifecycleOwner lifecycleOwner, Observer<Boolean> observer) {
        mdIsShowNavBar.observe(lifecycleOwner, observer);
    }

    public void setChosenMember(Member member) {
        chosenMember = member;
    }

    public Member getChosenMember() {
        return chosenMember;
    }
}
