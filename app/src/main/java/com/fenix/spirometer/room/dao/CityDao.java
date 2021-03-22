package com.fenix.spirometer.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fenix.spirometer.model.City;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SELECT * FROM City")
    List<City> getAllCities();

    @Query("SELECT * FROM City where proUid = :proUid")
    List<City> getCities(String proUid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(List<City> city);
}
