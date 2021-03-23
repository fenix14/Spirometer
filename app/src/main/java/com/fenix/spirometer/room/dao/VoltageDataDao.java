package com.fenix.spirometer.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.fenix.spirometer.room.model.SimpleHistory;
import com.fenix.spirometer.room.model.TestReportModel;
import com.fenix.spirometer.room.model.VoltageData;

import java.util.List;

@Dao
public interface VoltageDataDao {
    @Transaction
    @Query("select * from VoltageData")
    List<VoltageData> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<VoltageData> voltageDataList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(VoltageData voltageData);
}
