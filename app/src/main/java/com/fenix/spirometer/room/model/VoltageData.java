package com.fenix.spirometer.room.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"timeStamp"})})
public class VoltageData {
    @PrimaryKey
    @ForeignKey(entity = TestReportModel.class, parentColumns = "timeMills", childColumns = "timeStamp")
    public long timeStamp;

    public String dataAsJsonStr;

    public float FVC;
    public float FEV1;
    public float PEF;
    public float MVV;
    public float TLC;
    public float VC;

    public VoltageData(long timeStamp, String dataAsJsonStr, float FVC, float FEV1, float PEF, float MVV, float TLC, float VC) {
        this.timeStamp = timeStamp;
        this.dataAsJsonStr = dataAsJsonStr;
        this.FVC = FVC;
        this.FEV1 = FEV1;
        this.PEF = PEF;
        this.MVV = MVV;
        this.TLC = TLC;
        this.VC = VC;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getData() {
        return dataAsJsonStr;
    }

    public void setData(String data) {
        this.dataAsJsonStr = data;
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
