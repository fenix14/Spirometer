package com.fenix.spirometer.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.fenix.spirometer.model.Area;

import java.util.List;

@Dao
public interface AreaDao {
    @Query("select * from Area")
    List<Area> getAllAreas();
}
