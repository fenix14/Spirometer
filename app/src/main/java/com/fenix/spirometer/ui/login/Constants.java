package com.fenix.spirometer.ui.login;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Constants {
    // 登录账号格式
    public static final Pattern USER_NAME_PATTERN = Pattern.compile("^1[0-9]{10}$");
    // 登录密码最小长度
    public static final int PWD_LENGTH_MIN = 5;

}
