package com.fenix.spirometer.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.lang.reflect.Method;

public class TestReport extends BaseModel{
    private long timeMills;
    private Member member;
}
