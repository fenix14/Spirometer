package com.fenix.spirometer.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.lang.reflect.Method;

public class TestReport extends BaseModel{
    private long timeMills;
    private Member member;
    private String dataAsJsonStr;
    private Operator operator;
    private int specification;
    private float FVC;
    private float FEV1;
    private float PEF;
    private float MVV;
    private float TLC;
    private float VC;

    public TestReport(long timeMills, Member member, String dataAsJsonStr, Operator operator, int specification, float FVC, float FEV1, float PEF, float MVV, float TLC, float VC) {
        this.timeMills = timeMills;
        this.member = member;
        this.dataAsJsonStr = dataAsJsonStr;
        this.operator = operator;
        this.specification = specification;
        this.FVC = FVC;
        this.FEV1 = FEV1;
        this.PEF = PEF;
        this.MVV = MVV;
        this.TLC = TLC;
        this.VC = VC;
    }

    public long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(long timeMills) {
        this.timeMills = timeMills;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getData() {
        return dataAsJsonStr;
    }

    public void setData(String data) {
        this.dataAsJsonStr = data;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public int getSpecification() {
        return specification;
    }

    public void setSpecification(int specification) {
        this.specification = specification;
    }

    public String getDataAsJsonStr() {
        return dataAsJsonStr;
    }

    public void setDataAsJsonStr(String dataAsJsonStr) {
        this.dataAsJsonStr = dataAsJsonStr;
    }

    public float getFVC() {
        return FVC;
    }

    public void setFVC(float FVC) {
        this.FVC = FVC;
    }

    public float getFEV1() {
        return FEV1;
    }

    public void setFEV1(float FEV1) {
        this.FEV1 = FEV1;
    }

    public float getPEF() {
        return PEF;
    }

    public void setPEF(float PEF) {
        this.PEF = PEF;
    }

    public float getMVV() {
        return MVV;
    }

    public void setMVV(float MVV) {
        this.MVV = MVV;
    }

    public float getTLC() {
        return TLC;
    }

    public void setTLC(float TLC) {
        this.TLC = TLC;
    }

    public float getVC() {
        return VC;
    }

    public void setVC(float VC) {
        this.VC = VC;
    }
}
