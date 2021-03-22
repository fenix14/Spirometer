package com.fenix.spirometer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fenix.spirometer.model.Province;

import java.util.List;

@Dao
public interface ProvinceDao {
    @Query("SELECT * FROM Province")
    LiveData<List<Province>> getAllProvinces();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProvinces(List<Province> provincesModels);
}
