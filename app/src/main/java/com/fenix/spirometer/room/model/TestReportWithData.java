package com.fenix.spirometer.room.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;

public class TestReportWithData {
    @Embedded
    public TestReportModel testReportModel;

    @Relation(
            parentColumn = "memberId",
            entityColumn = "id")
    public Member member;

    @Relation(
            parentColumn = "operatorId",
            entityColumn = "userId")
    public Operator operator;

    @Relation(
            parentColumn = "timeMills",
            entity = VoltageData.class,
            entityColumn = "timeStamp",
            projection = "dataAsJsonStr")
    public String voltageData;
}
