package com.fenix.spirometer.room;

import androidx.lifecycle.LiveData;

import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.room.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetectCompRepository {
    private static volatile DetectCompRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor;

    public DetectCompRepository() {
        database = AppDatabase.getInstance();
        executor = Executors.newCachedThreadPool();
    }

    public static DetectCompRepository getInstance() {
        if (instance == null) {
            instance = new DetectCompRepository();
        }
        return instance;
    }

    public LiveData<List<DetectorCompensation>> getAll() {
        return database.detectCompDao().getAll();
    }

    public void insertCompensations(List<DetectorCompensation> values) {
        executor.execute(() -> database.detectCompDao().insert(values));
    }

    public void updateCompensation(DetectorCompensation value) {
        executor.execute(() -> database.detectCompDao().update(value));
    }
}
