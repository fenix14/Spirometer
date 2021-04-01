package com.fenix.spirometer.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.lang.reflect.Method;

public class TestReport extends BaseModel{
    private long timeMills;
    private Member member;
    private String dataAsJsonStr;
    private Operator operator;
    private int specification;

    public TestReport(long timeMills, Member member, String dataAsJsonStr, Operator operator, int specification) {
        this.timeMills = timeMills;
        this.member = member;
        this.dataAsJsonStr = dataAsJsonStr;
        this.operator = operator;
        this.specification = specification;
    }

    public long getTimeMills() {
        return timeMills;
    }

    public void setTimeMills(long timeMills) {
        this.timeMills = timeMills;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getData() {
        return dataAsJsonStr;
    }

    public void setData(String data) {
        this.dataAsJsonStr = data;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public int getSpecification() {
        return specification;
    }

    public void setSpecification(int specification) {
        this.specification = specification;
    }
}
