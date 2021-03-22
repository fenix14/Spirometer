package com.fenix.spirometer.ui.login;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.model.LoginState;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.model.Province;
import com.fenix.spirometer.room.AddrRepository;
import com.fenix.spirometer.room.DetectCompRepository;
import com.fenix.spirometer.room.EstValueRepository;
import com.fenix.spirometer.room.MemberRepository;
import com.fenix.spirometer.room.OperatorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * VM：登录页面
 */
public class LoginViewModel extends ViewModel {
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private final MemberRepository memberRepo;
    private final AddrRepository addrRepo;
    private final OperatorRepository operRepo;
    private final DetectCompRepository detectCompRepo;
    private final EstValueRepository estValueRepo;
    public LoginViewModel() {
        memberRepo = MemberRepository.getInstance();
        addrRepo = AddrRepository.getInstance();
        operRepo = OperatorRepository.getInstance();
        detectCompRepo = DetectCompRepository.getInstance();
        estValueRepo = EstValueRepository.getInstance();
    }

    public void login(String userId, String password) {
        operRepo.login(userId, password);
    }

    public void subscribeToLoginState(LifecycleOwner lifecycleOwner, Observer<LoginState> observer) {
        operRepo.getLoginState().observe(lifecycleOwner, observer);
    }

    // 调试初始化数据用，后续需根据数据预置方式调整
    public void insertProvinces(List<Province> provinces) {
        addrRepo.initAddress(provinces);
    }

    public void insertDetectorCompensations(List<DetectorCompensation> compensations) {
        detectCompRepo.insertCompensations(compensations);
    }

    public void insertEstValues() {
        List<EstValue> estValues = new ArrayList<>();
        //用力肺活量
        estValues.add(new EstValue("FVC", true, "国标名称/欧标名称1男", 0));
        estValues.add(new EstValue("FVC", false, "国标名称/欧标名称1女", 1));
        // 1秒量
        estValues.add(new EstValue("FEV1", true, "国标名称/欧标名称2男", 0));
        estValues.add(new EstValue("FEV1", false, "国标名称/欧标名称2女", 1));
        // 最大通气量
        estValues.add(new EstValue("MVV", true, "国标名称/欧标名称3男", 0));
        estValues.add(new EstValue("MVV", false, "国标名称/欧标名称3女", 1));
        estValues.add(new EstValue("PEF", true, "国标名称/欧标名称4男", 0));
        estValues.add(new EstValue("PEF", false, "国标名称/欧标名称4女", 1));
        estValues.add(new EstValue("VC", true, "国标名称/欧标名称5男", 0));
        estValues.add(new EstValue("VC", false, "国标名称/欧标名称5女", 1));
        estValues.add(new EstValue("TLC", true, "国标名称/欧标名称6男", 0));
        estValues.add(new EstValue("TLC", false, "国标名称/欧标名称6女", 1));
        estValueRepo.insertEstiValue(estValues);
    }

    public void insertOperators() {
        List<Operator> operators = new ArrayList<>();
        operators.add(new Operator("18674018759", "贺冯飞", "123456", "管理员", "123123asdasd", true));
        operators.add(new Operator("12345678901", "吴鑫", "654321", "院长", "123123asdasd", false));
        operators.add(new Operator("98765432109", "尼戈姆莎买买提", "123456sdfsdfdsfsdfdsf7890", "护士长", "123123asdasd", false));
        operators.add(new Operator("11111111111", "TEST", "password123", "外科主任", "123123asdasd", false));
        operRepo.insertOperator(operators);
    }
}