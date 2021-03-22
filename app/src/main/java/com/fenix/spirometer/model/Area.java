package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = City.class, parentColumns = "uid", childColumns = "couUid"),
        indices = {@Index(value = {"uid"}, unique = true), @Index(value = {"couUid"})})
public class Area extends BaseModel {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "couUid")
    @NonNull
    private String couUid;

    public Area(@NonNull String uid, String name, String couUid) {
        this.uid = uid;
        this.name = name;
        this.couUid = couUid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCouUid() {
        return couUid;
    }

    public void setCouUid(String couUid) {
        this.couUid = couUid;
    }
}
