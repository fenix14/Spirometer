package com.fenix.spirometer.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 管理员数据模型
 */
@Entity
public class Operator extends BaseModel {
    // 账号，目前是手机号
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "userId")
    private String userId;

    // 显示名
    @NonNull
    @ColumnInfo(name = "displayName")
    private String displayName;

    // 密码
    @NonNull
    @ColumnInfo(name = "password")
    private String password;

    // 职务
    @NonNull
    @ColumnInfo(name = "duty")
    private String duty;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "isAdmin", defaultValue = "false")
    private boolean isAdmin;

    @Ignore
    public Operator() {
    }

    public Operator(@NonNull String userId, @NonNull String displayName, @NonNull String password, @NonNull String duty, @NonNull String description, boolean isAdmin) {
        this.userId = userId;
        this.displayName = displayName;
        this.password = password;
        this.duty = duty;
        this.description = description;
        this.isAdmin = isAdmin;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NonNull String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @NonNull
    public String getDuty() {
        return duty;
    }

    public void setDuty(@NonNull String duty) {
        this.duty = duty;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isSameOperator(Operator another) {
        if (another == null) {
            return false;
        }
        return another.userId.equals(userId) && another.password.equals(password);
    }

    public boolean isSameOperator(String userId, String password) {
        return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(password) && userId.equals(this.userId) && password.equals(this.password);
    }
}
