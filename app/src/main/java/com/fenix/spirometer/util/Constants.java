package com.fenix.spirometer.util;

import android.os.Environment;

import androidx.annotation.IntDef;

import java.util.regex.Pattern;

public class Constants {
    // 登录账号格式
    public static final Pattern USER_NAME_PATTERN = Pattern.compile("^1[0-9]{10}$");
    // 登录密码最小长度
    public static final int PWD_LENGTH_MIN = 5;

    public static final int REQUEST_CODE_LOGIN = 0;
    public static final int RESULT_CODE_SUCCESS = 0;
    public static final String SP_NAME = "app";
    public static final String SP_KEY_IS_INITIALIZED = "isInitialized";

    public static final String BUNDLE_KEY_TIME_STAMP = "reportTimeStamp";
    public static final String BUNDLE_KEY_TIME_STAMPS = "TimeStampForCompare";
    public final static String STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/Spirometer/";
    public final static String EXCEL_PATH = STORAGE_PATH + "excels/";
    public final static String VIDEO_PATH = STORAGE_PATH + "video/";


    @IntDef({TrainingType.STANDARD, TrainingType.VITAL_CAPACITY, TrainingType.DIASTOLIC})
    public @interface TrainingType {
        public int STANDARD = 0;
        public int VITAL_CAPACITY = 1;
        public int DIASTOLIC = 2;
        public int PET = 3;
    }

    @IntDef({BgType.TYPE_DARK, BgType.TYPE_LIGHT, BgType.TYPE_GONE})
    public @interface BgType {
        public int TYPE_DARK = 0;
        public int TYPE_LIGHT = 1;
        public int TYPE_GONE = 2;
    }

    @IntDef({FooterType.TYPE_NONE, FooterType.TYPE_NAV, FooterType.TYPE_ONE_BTN, FooterType.TYPE_THREE_BTN})
    public @interface FooterType {
        public int TYPE_NONE = 0;
        public int TYPE_NAV = 1;
        public int TYPE_ONE_BTN = 2;
        public int TYPE_THREE_BTN = 3;
    }

    @IntDef({Gender.MALE, Gender.FEMALE})
    public @interface Gender {
        public int MALE = 0;
        public int FEMALE = 1;
    }
}
