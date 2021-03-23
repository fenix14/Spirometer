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

    @Query("select * from Member where id = :id")
    LiveData<Member> getMember(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Member member);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Member> members);

    @Update
    void update(Member member);
}
