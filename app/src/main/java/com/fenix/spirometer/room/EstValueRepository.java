package com.fenix.spirometer.room;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.room.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EstValueRepository {
    private static volatile EstValueRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor;

    public EstValueRepository() {
        database = AppDatabase.getInstance();
        executor = Executors.newCachedThreadPool();
    }

    public static EstValueRepository getInstance() {
        if (instance == null) {
            instance = new EstValueRepository();
        }
        return instance;
    }

    public LiveData<List<EstValue>> getAll() {
        return database.estiValueDao().getAll();
    }

    public LiveData<List<EstValue>> getAllByGender(boolean isMale) {
        return database.estiValueDao().getAllByGender(isMale);
    }

    public void insertEstiValue(EstValue value) {
        executor.execute(() -> database.estiValueDao().insert(value));
    }

    public void insertEstiValue(List<EstValue> values) {
        executor.execute(() -> database.estiValueDao().insert(values));
    }

    public void updateEstiValue(EstValue value) {
        executor.execute(() -> database.estiValueDao().update(value));
    }
}
