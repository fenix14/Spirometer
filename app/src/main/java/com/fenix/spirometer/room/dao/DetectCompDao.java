package com.fenix.spirometer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.fenix.spirometer.model.DetectorCompensation;

import java.util.List;

@Dao
public interface DetectCompDao {
    @Query("select * from DetectorCompensation ORDER BY voltageLow")
    LiveData<List<DetectorCompensation>> getAll();

    @Query("select * from DetectorCompensation where voltageLow >= :voltageLow and voltageHigh <= :voltageHigh ORDER BY voltageLow")
    LiveData<List<DetectorCompensation>> getAllByRange(int voltageLow, int voltageHigh);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DetectorCompensation compensation);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<DetectorCompensation> compensation);

    @Update
    void update(DetectorCompensation compensation);
}
