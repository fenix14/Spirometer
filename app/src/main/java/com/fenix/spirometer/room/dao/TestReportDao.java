package com.fenix.spirometer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;

import com.fenix.spirometer.room.model.History;
import com.fenix.spirometer.room.model.SimpleHistory;
import com.fenix.spirometer.room.model.TestReportModel;
import com.fenix.spirometer.room.model.VoltageData;

import java.util.List;

@Dao
public interface TestReportDao {
    @Transaction
    @Query("select * from TestReportModel")
    List<History> getAll();

    @Transaction
    @Query("select * from TestReportModel")
    LiveData<List<SimpleHistory>> getAllSimple();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTestReports(List<TestReportModel> testReports);
}
