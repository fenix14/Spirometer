package com.fenix.spirometer.room.util;

import android.text.TextUtils;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.room.model.TestReportModel;
import com.fenix.spirometer.room.model.TestReportWithData;
import com.fenix.spirometer.room.model.VoltageData;

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
        String cellphone = member.getId();
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
                model.getWeight(), model.getId(), model.getProvince(), model.getCity(),
                model.getCounty(), model.getArea());
    }

    public static TestReportModel object2Model(TestReport report) {
        return new TestReportModel(report.getTimeMills(), report.getMember().getId(), report.getOperator().getUserId());
    }

    public static TestReport model2Object(TestReportWithData model) {
        VoltageData voltageData = model.voltageData;
        if (voltageData == null) {
            return new TestReport(model.testReportModel.getTimeMills(), model.member, "",
                    model.operator, 0, 0, 0, 0, 0, 0, 0);
        } else {
            return new TestReport(model.testReportModel.getTimeMills(), model.member, model.voltageData.dataAsJsonStr,
                    model.operator, 0, voltageData.FVC, voltageData.FEV1, voltageData.PEF, voltageData.MVV, voltageData.TLC, voltageData.VC);
        }

    }
}
