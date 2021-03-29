package com.fenix.spirometer.ble;

public class MeasureData {
    private long timeStamp;
    private int[] voltages;

    public MeasureData(long timeStamp, int[] voltages) {
        this.timeStamp = timeStamp;
        this.voltages = voltages;
    }


}
