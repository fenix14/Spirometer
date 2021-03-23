package com.fenix.spirometer.room.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;

@Entity(foreignKeys = {
        @ForeignKey(entity = Member.class, parentColumns = "id", childColumns = "memberId"),
        @ForeignKey(entity = Operator.class, parentColumns = "userId", childColumns = "operatorId")
},
        indices = {@Index(value = {"memberId"}), @Index(value = {"operatorId"})})
public class TestReportModel {
    @PrimaryKey
    private long timeMills;
    private String memberId;
    private String operatorId;

    public TestReportModel(long timeMills, String memberId, String operatorId) {
        this.timeMills = timeMills;
        this.memberId = memberId;
        this.operatorId = operatorId;
    }

    public long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(long timeMills) {
        this.timeMills = timeMills;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
}
