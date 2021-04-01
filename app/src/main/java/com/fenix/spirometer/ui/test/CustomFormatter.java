package com.fenix.spirometer.ui.test;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Arrays;

public class CustomFormatter extends DefaultAxisValueFormatter {
    private final static int[] title = new int[]{2000 , 4000, 6000};

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     *
     * @param digits
     */
    public CustomFormatter(int digits) {
        super(digits);
    }

    @Override
    public String getFormattedValue(float value) {
        return String.valueOf(value);
    }

    /**
     * Used to draw axis labels, calls {@link #getFormattedValue(float)} by default.
     *
     * @param value float to be formatted
     * @param axis  axis being labeled
     * @return formatted string label
     */
    public String getAxisLabel(float value, AxisBase axis) {
        int valueInt = (int)value;
        int index = Arrays.binarySearch(title, valueInt);
        return index >= 0 ? String.valueOf(title[index]/100) : "";
    }
}
