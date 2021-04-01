package com.fenix.spirometer.ble;

public class MeasureData {
    public long timeStamp;
    public int[] voltages;

    public MeasureData(long timeStamp, int[] voltages) {
        this.timeStamp = timeStamp;
        this.voltages = voltages;
    }
}
