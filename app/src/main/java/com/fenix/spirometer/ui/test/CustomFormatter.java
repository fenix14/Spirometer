package com.fenix.spirometer.ui.test;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Arrays;

public class CustomFormatter extends DefaultAxisValueFormatter {
    private final static int[] title = new int[]{0, 20000 , 40000, 60000};

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
        Log.d("hff", "getFormattedValue.index = " + index + ", valueInt = " + valueInt);
        return index >= 0 ? String.valueOf(title[index]/1000) : "";
    }
}
