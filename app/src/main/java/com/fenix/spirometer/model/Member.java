package com.fenix.spirometer.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 受试人员数据模型
 */
@Entity(primaryKeys = {"id"})
public class Member extends BaseModel{
    @NonNull
    private String name;
    @NonNull
    private String gender;
    @NonNull
    private String age;
    @NonNull
    private String weight;
    @NonNull
    private String height;
    @NonNull
    private String id;
    @ColumnInfo(defaultValue = "")
    private String province = "";
    @ColumnInfo(defaultValue = "")
    private String city = "";
    @ColumnInfo(defaultValue = "")
    private String county = "";
    @ColumnInfo(defaultValue = "")
    private String area = "";

    @Ignore
    public Member() {
    }

    public Member(@NotNull String name, @NotNull String gender, @NotNull String age, @NotNull String weight, @NotNull String height, String id, String province, String city, String county, String area) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.id = id;
        this.province = province;
        this.city = city;
        this.county = county;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvinceCity() {
        return this.province.concat(city);
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return name.equals(member.name) &&
                gender.equals(member.gender) &&
                age.equals(member.age) &&
                weight.equals(member.weight) &&
                height.equals(member.height) &&
                id.equals(member.id) &&
                Objects.equals(province, member.province) &&
                Objects.equals(city, member.city) &&
                Objects.equals(county, member.county) &&
                Objects.equals(area, member.area);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender, age, weight, height, id, province, city, county, area);
    }
}
