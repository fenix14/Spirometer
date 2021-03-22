package com.fenix.spirometer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;

import com.fenix.spirometer.R;
import com.fenix.spirometer.app.MyApplication;

public class Utils {
    public static boolean isUserIdValid(String userId) {
        return Constants.USER_NAME_PATTERN.matcher(userId).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > Constants.PWD_LENGTH_MIN;
    }

    public static String getAppVersionName() {
        PackageManager pm = MyApplication.getInstance().getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName != null ? pi.versionName : "";
    }

    public static AlertDialog createConfirmDialog(Context context, String content, DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(context).setMessage(content)
                .setPositiveButton(R.string.dialog_ok, listener)
                .setNegativeButton(R.string.dialog_cancel, listener)
                .create();
    }
}
