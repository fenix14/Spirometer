package com.fenix.spirometer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.fenix.spirometer.model.Member;

import java.util.List;

@Dao
public interface MemberDao {
    @Query("select * from Member ORDER BY name")
    LiveData<List<Member>> getAllMembers();

    @Query("select * from Member where cellphone = :cellphone AND name = :name")
    LiveData<Member> getMember(String cellphone, String name);

    @Query("delete FROM Member")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Member member);

    @Update
    void update(Member member);
}
