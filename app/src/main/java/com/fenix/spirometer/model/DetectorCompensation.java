package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices ={@Index(value = {"voltageLow", "voltageHigh"}, unique = true)})
public class DetectorCompensation extends BaseModel {
    @PrimaryKey
    private int voltageLow;

    @ColumnInfo
    private int voltageHigh;

    @ColumnInfo
    private float compensation;

    @Ignore // for preference
    public DetectorCompensation() {
    }

    @Ignore // for xml parser
    public DetectorCompensation(String range, String compensation) {
        setVoltageRange(range);
        setCompensation(compensation);
    }

    public DetectorCompensation(int voltageLow, int voltageHigh, float compensation) {
        this.voltageLow = voltageLow;
        this.voltageHigh = voltageHigh;
        this.compensation = compensation;
    }

    public int getVoltageLow() {
        return voltageLow;
    }

    public void setVoltageLow(int voltageLow) {
        this.voltageLow = voltageLow;
    }

    public String getVoltageRange() {
        return voltageLow + "~" + voltageHigh;
    }

    public void setVoltageRange(@NonNull String rangeStr) {
        String[] range = rangeStr.split("~");
        if (range.length >= 2) {
            voltageLow = Integer.parseInt(range[0]);
            voltageHigh = Integer.parseInt(range[1]);
        }
    }

    public int getVoltageHigh() {
        return voltageHigh;
    }

    public void setVoltageHigh(int voltageHigh) {
        this.voltageHigh = voltageHigh;
    }

    public float getCompensation() {
        return compensation;
    }

    public void setCompensation(float compensation) {
        this.compensation = compensation;
    }

    public void setCompensation(String compensation) {
        this.compensation = Float.parseFloat(compensation);
    }
}
