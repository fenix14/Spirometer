package com.fenix.spirometer.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.room.model.TestReportModel;
import com.fenix.spirometer.util.Utils;

public class SimpleReport extends BaseModel {
    @Embedded
    public TestReportModel testReportModel;

    @Relation(
            parentColumn = "memberId",
            entity = Member.class,
            entityColumn = "id",
            projection = "name"
    )
    public String memberName;

    @Relation(
            parentColumn = "memberId",
            entity = Member.class,
            entityColumn = "id",
            projection = "gender"
    )
    public String gender;

    @Relation(
            parentColumn = "memberId",
            entity = Member.class,
            entityColumn = "id",
            projection = "age"
    )
    public String age;

    public SimpleReport(TestReportModel testReportModel, String memberName, String gender, String age) {
        this.testReportModel = testReportModel;
        this.memberName = memberName;
        this.gender = gender;
        this.age = age;
    }

    public TestReportModel getTestReportModel() {
        return testReportModel;
    }

    public void setTestReportModel(TestReportModel testReportModel) {
        this.testReportModel = testReportModel;
    }

    public long getTimeMills() {
        return testReportModel.getTimeMills();
    }

    public String getDate() {
        return Utils.getDateByMills(testReportModel.getTimeMills());
    }

    public String getTime() {
        return Utils.getTimeByMills(testReportModel.getTimeMills());
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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
}
