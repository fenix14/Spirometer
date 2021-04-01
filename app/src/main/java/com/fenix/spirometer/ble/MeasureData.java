package com.fenix.spirometer.ble;

public class MeasureData {
    private long timeStamp;
    private int[] voltages;

    public MeasureData(long timeStamp, int[] voltages) {
        this.timeStamp = timeStamp;
        this.voltages = voltages;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int[] getVoltages() {
        return voltages;
    }

    public void setVoltages(int[] voltages) {
        this.voltages = voltages;
    }
}
