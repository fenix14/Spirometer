package com.fenix.spirometer.room.util;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.util.Arrays;

public class VoltageDataConverter {
    @TypeConverter
    public static int[] revert(String dataStr) {
        if (dataStr == null || dataStr.isEmpty()) {
            return new int[0];
        }
        String[] str = dataStr.split(",");
        if (str.length == 0) {
            return new int[0];
        }
        return Arrays.stream(str).mapToInt(Integer::parseInt).toArray();
    }

    @TypeConverter
    public static String convert(@NonNull int[] data) {
        if (data.length == 0) {
            return "";
        }
        int iMax = data.length - 1;
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; ; i++) {
            sb.append(data[i]);
            if (i == iMax) {
                return sb.toString();
            }
            sb.append(", ");
        }
    }
}
