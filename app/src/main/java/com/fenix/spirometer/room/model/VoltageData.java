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

    public VoltageData(long timeStamp, String dataAsJsonStr) {
        this.timeStamp = timeStamp;
        this.dataAsJsonStr = dataAsJsonStr;
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
}
