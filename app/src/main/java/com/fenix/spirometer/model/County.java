package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = City.class, parentColumns = "uid", childColumns = "citUid"),
        indices = {@Index(value = {"uid"}, unique = true), @Index(value = {"citUid"})})
public class County {
    public static final County EMPTY_COUNTY = new County("", " ", " ");

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;
    @ColumnInfo(name = "citUid")

    @NonNull
    private String citUid;

//    private List<AreaModel> areas;

    public County() {
    }

    @Ignore
    public County(@NonNull String uid, @NonNull String name, @NonNull String citUid) {
        this.uid = uid;
        this.name = name;
        this.citUid = citUid;
    }

    //    public CountyModel(@NonNull String uid, @NonNull String name, @NonNull String citUid, List<AreaModel> areas) {
//        this.uid = uid;
//        this.name = name;
//        this.citUid = citUid;
//        this.areas = areas;
//    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getCitUid() {
        return citUid;
    }

    public void setCitUid(@NonNull String citUid) {
        this.citUid = citUid;
    }

//    public List<AreaModel> getAreas() {
//        return areas;
//    }
//
//    public void setAreas(List<AreaModel> areas) {
//        this.areas = areas;
//    }

    @Override
    public String toString() {
        return name;
    }
}
