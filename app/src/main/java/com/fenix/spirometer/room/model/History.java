package com.fenix.spirometer.room.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;

public class History {
    @Embedded
    public TestReportModel testReportModel;

    @Relation(parentColumn = "memberId", entity = Member.class, entityColumn = "id")
    public Member member;

    @Relation(parentColumn = "operatorId", entity = Operator.class, entityColumn = "userId")
    public Operator operator;

    @Relation(parentColumn = "timeMills",  entity = VoltageData.class, entityColumn = "timeStamp")
    public VoltageData data;
}
