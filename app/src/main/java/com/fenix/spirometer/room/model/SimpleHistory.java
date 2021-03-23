package com.fenix.spirometer.room.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fenix.spirometer.model.Member;

public class SimpleHistory {
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

    public SimpleHistory(TestReportModel testReportModel, String memberName, String gender, String age) {
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
