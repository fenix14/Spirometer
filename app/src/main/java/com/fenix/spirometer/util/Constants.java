package com.fenix.spirometer.util;

import androidx.annotation.IntDef;

public class Constants {

    @IntDef({TrainingType.STANDARD, TrainingType.VITAL_CAPACITY, TrainingType.DIASTOLIC})
    public @interface TrainingType {
        public int STANDARD = 0;
        public int VITAL_CAPACITY = 1;
        public int DIASTOLIC = 2;
    }

    @IntDef({BgType.TYPE_DARK, BgType.TYPE_LIGHT, BgType.TYPE_GONE})
    public @interface BgType {
        public int TYPE_DARK = 0;
        public int TYPE_LIGHT = 1;
        public int TYPE_GONE = 2;
    }
}
