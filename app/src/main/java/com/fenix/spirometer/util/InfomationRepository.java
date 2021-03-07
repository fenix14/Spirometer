package com.fenix.spirometer.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.fenix.spirometer.app.MyApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InfomationRepository {
    private static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /*
     * Get the STB MacAddress
     */
    public static  String getMacAddress(){
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
}
