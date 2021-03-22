package com.fenix.spirometer.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fenix.spirometer.model.County;

import java.util.List;

@Dao
public interface CountyDao {
    @Query("SELECT * FROM County")
    List<County> getAllCounties();

    @Query("SELECT * FROM County where citUid = :citUid")
    List<County> getCounties(String citUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCounties(List<County> counties);
}
