package com.fenix.spirometer.ble;

public class MeasureData {
    public long timeStamp;
    public int[] flow;

    public MeasureData() {
    }

    public MeasureData(long timeStamp, int[] flow) {
        this.timeStamp = timeStamp;
        this.flow = flow;
    }
}
