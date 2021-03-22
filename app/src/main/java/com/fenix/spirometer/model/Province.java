package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fenix.spirometer.model.City.EMPTY_CITY;

@Entity(indices = @Index(value = {"uid"}, unique = true))
public class Province {
    public static final Province EMPTY_PROVINCE = new Province(" ", " ", Arrays.asList(EMPTY_CITY));

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    @Ignore
    private List<City> cities;

    public Province() {
    }

    @Ignore
    public Province(@NonNull String uid, @NonNull String name, List<City> cities) {
        this.uid = uid;
        this.name = name;
        this.cities = new ArrayList<>();
        this.cities.addAll(cities);
    }

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

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return name;
    }
}
