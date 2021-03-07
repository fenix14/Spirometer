package com.fenix.spirometer.room.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * 管理员数据模型
 */
@Entity
public class AdminModel {
    @PrimaryKey(autoGenerate = true)
    private long id;

    // 账号，目前是手机号
    private String userId;

    // 显示名
    @ColumnInfo(defaultValue = "")
    private String displayName;

    // 密码
    private String password;

    // 职务
    private String duty;

    public AdminModel(String userId, String displayName, String password, String duty) {
        this.userId = userId;
        this.displayName = displayName;
        this.password = password;
        this.duty = duty;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
