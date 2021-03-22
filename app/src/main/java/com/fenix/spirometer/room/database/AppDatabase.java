package com.fenix.spirometer.room.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.model.Area;
import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.model.City;
import com.fenix.spirometer.model.County;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Province;
import com.fenix.spirometer.room.dao.CityDao;
import com.fenix.spirometer.room.dao.CountyDao;
import com.fenix.spirometer.room.dao.DetectCompDao;
import com.fenix.spirometer.room.dao.EstiValueDao;
import com.fenix.spirometer.room.dao.MemberDao;
import com.fenix.spirometer.room.dao.OperatorDao;
import com.fenix.spirometer.room.dao.ProvinceDao;

/**
 * 数据库db
 */
@Database(entities = {DetectorCompensation.class, EstValue.class, Operator.class, Member.class, Province.class, City.class, County.class, Area.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "database";

    public abstract OperatorDao operatorDao();

    public abstract MemberDao memberDao();

    public abstract ProvinceDao provinceDao();

    public abstract CityDao cityDao();

    public abstract CountyDao countyDao();

    public abstract EstiValueDao estiValueDao();

    public abstract DetectCompDao detectCompDao();

    private volatile static AppDatabase sInstance;

    public static AppDatabase getInstance() {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(MyApplication.getInstance(), AppDatabase.class, DB_NAME).build();
        }
        return sInstance;
    }
}
