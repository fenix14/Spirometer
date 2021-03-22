package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fenix.spirometer.model.County.EMPTY_COUNTY;

@Entity(foreignKeys = @ForeignKey(entity = Province.class, parentColumns = "uid", childColumns = "proUid"),
        indices = {@Index(value = {"uid"}, unique = true), @Index(value = {"proUid"})})
public class City extends BaseModel {
    public static final City EMPTY_CITY = new City(" ", " ", Arrays.asList(EMPTY_COUNTY), " ");

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @ColumnInfo(name = "proUid")
    @NonNull
    private String proUid;

    @Ignore
    @Relation(
            parentColumn = "uid",
            entityColumn = "citUid"
    )
    private List<County> counties;

    public City() {
    }

    public City(@NonNull String uid, @NonNull String name, List<County> counties, @NonNull String proUid) {
        this.uid = uid;
        this.name = name;
        this.counties = new ArrayList<>();
        this.counties.addAll(counties);
        this.proUid = proUid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uId) {
        this.uid = uId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getProUid() {
        return proUid;
    }

    public void setProUid(@NonNull String proUid) {
        this.proUid = proUid;
    }

    public List<County> getCounties() {
        return counties;
    }

    public void setCounties(List<County> counties) {
        this.counties = counties;
    }

    @Override
    public String toString() {
        return name;
    }
}
