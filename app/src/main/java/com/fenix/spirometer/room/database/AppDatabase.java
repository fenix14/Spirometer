package com.fenix.spirometer.room.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.room.bean.AdminModel;
import com.fenix.spirometer.room.dao.AdminDao;

/**
 * 数据库db
 */
@Database(entities = {AdminModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "database";

    public abstract AdminDao adminDao();

    private volatile static AppDatabase sInstance;

    public static AppDatabase getInstance() {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(MyApplication.getInstance(), AppDatabase.class, DB_NAME).build();
        }
        return sInstance;
    }
}
