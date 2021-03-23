package com.fenix.spirometer.room.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"timeStamp"})})
public class VoltageData {
    @PrimaryKey
    @ForeignKey(entity = TestReportModel.class, parentColumns = "timeMills", childColumns = "timeStamp")
    long timeStamp;

    int[] data;

    public VoltageData(long timeStamp, int[] data) {
        this.timeStamp = timeStamp;
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }
}
