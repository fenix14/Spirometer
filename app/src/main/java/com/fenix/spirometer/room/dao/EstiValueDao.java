package com.fenix.spirometer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.fenix.spirometer.model.EstValue;

import java.util.List;

@Dao
public interface EstiValueDao {
    @Query("select * from EstValue")
    LiveData<List<EstValue>> getAll();


    @Query("select * from EstValue where isMale = :isMale")
    LiveData<List<EstValue>> getAllByGender(boolean isMale);

    @Delete
    void delete(EstValue estValue);

    @Update
    int update(EstValue estValue);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(EstValue estValue);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<EstValue> estValues);
}
