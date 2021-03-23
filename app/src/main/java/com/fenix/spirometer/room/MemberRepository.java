package com.fenix.spirometer.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.room.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemberRepository {
    private static volatile MemberRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor;
    private MutableLiveData<List<com.fenix.spirometer.model.Member>> mdMembers;

    public MemberRepository() {
        database = AppDatabase.getInstance();
        executor = Executors.newCachedThreadPool();
    }

    public static MemberRepository getInstance() {
        if (instance == null) {
            instance = new MemberRepository();
        }
        return instance;
    }

    public LiveData<List<Member>> getAllMembers() {
        return database.memberDao().getAllMembers();
    }

    public void insertMember(Member member) {
        executor.execute(() -> database.memberDao().insert(member));
    }

    public void insertMembers(List<Member> members) {
        executor.execute(() -> database.memberDao().insert(members));
    }

    public void insertOrUpdate(Member member) {
        executor.execute(() -> {
            if (database.memberDao().insert(member) < 0) {
                database.memberDao().update(member);
            }
        });
    }

    public void updateMember(Member member) {
        executor.execute(() -> database.memberDao().update(member));
    }

    public LiveData<Member> getMember(String id) {
        return database.memberDao().getMember(id);
    }
}
