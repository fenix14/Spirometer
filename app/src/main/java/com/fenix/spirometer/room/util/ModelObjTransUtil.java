package com.fenix.spirometer.room.util;

import android.text.TextUtils;

import com.fenix.spirometer.model.Member;

public class ModelObjTransUtil {

    public static Member object2Model(com.fenix.spirometer.model.Member member) {
        if (member == null) {
            return null;
        }
        String name = member.getName();
        String gender = member.getGender();
        String age = member.getAge();
        String weight = member.getWeight();
        String height = member.getHeight();
        String cellphone = member.getCellphone();
        String province = member.getProvince();
        String city = member.getCity();
        String county = member.getCounty();
        String area = member.getArea();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(age)
                || TextUtils.isEmpty(weight) || TextUtils.isEmpty(height)) {
            return null;
        }
        return new Member(name, gender, age, weight, height, cellphone, province, city, county, area);
    }


    public static com.fenix.spirometer.model.Member model2Object(Member model) {
        return new com.fenix.spirometer.model.Member(model.getName(), model.getGender(), model.getAge(), model.getHeight(),
                model.getWeight(), model.getCellphone(), model.getProvince(), model.getCity(),
                model.getCounty(), model.getArea());
    }

}
