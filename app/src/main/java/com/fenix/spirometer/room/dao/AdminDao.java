package com.fenix.spirometer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fenix.spirometer.room.bean.AdminModel;

import java.util.List;

/**
 * 管理员Dao
 */
@Dao
public interface AdminDao {
    @Query("select * from AdminModel")
    List<AdminModel> getAllUser();

    @Query("delete FROM AdminModel")
    void deleteAll();

    @Query("select * FROM AdminModel WHERE userId= :userId")
    AdminModel getAdmin(String userId);

    @Query("select * FROM AdminModel WHERE userId= :userId AND password= :password")
    AdminModel getAdmin(String userId, String password);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AdminModel... administrator);
}
