package com.fenix.spirometer.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.fenix.spirometer.model.Operator;

import java.util.List;

@Dao
public interface OperatorDao {
    @Query("select * from Operator ORDER BY isAdmin DESC, userId DESC")
    List<Operator> getOperators();

    @Query("select * from Operator where userId = :userId and password = :password")
    Operator getOperator(String userId, String password);

    @Query("select * from Operator where userId = :userId")
    Operator getOperator(String userId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOperator(Operator operator);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOperators(List<Operator> operators);

    @Update
    int updateOperator(Operator operator);

    @Delete
    void deleteOperator(Operator operator);
}
