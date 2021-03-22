package com.fenix.spirometer.model;

public class TestInfo extends BaseModel {
    private Member member;
    private boolean isTesting;

    public TestInfo() {
    }

    public TestInfo(Member member, boolean isTesting) {
        this.member = member;
        this.isTesting = isTesting;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }
}
