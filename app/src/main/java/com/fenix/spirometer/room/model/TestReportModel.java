package com.fenix.spirometer.room.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;

@Entity(foreignKeys = {
        @ForeignKey(entity = Member.class, parentColumns = "userId", childColumns = "userId"),
        @ForeignKey(entity = Operator.class, parentColumns = "userId", childColumns = "operatorId")
},
        indices = {@Index(value = {"uid"}, unique = true), @Index(value = {"proUid"})})
public class TestReportModel {
    private long timeMills;
    private String  userId;
    private byte[] data;
}
