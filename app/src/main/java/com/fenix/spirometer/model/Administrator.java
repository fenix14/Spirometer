package com.fenix.spirometer.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 管理员业务模型
 */
public class Administrator implements Serializable {
    private String adminId;
    private String displayName;
    private String password;
    private String duty;

    public Administrator(String adminId, String displayName, String password, String duty) {
        this.adminId = adminId;
        this.displayName = displayName;
        this.password = password;
        this.duty = duty;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public boolean isSameAdmin(Administrator another) {
        if (another == null) {
            return false;
        }
        return another.adminId.equals(adminId) && another.password.equals(password);
    }

    public boolean isSameAdmin(String userName, String password) {
        return !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) && userName.equals(adminId) && password.equals(password);
    }
}
