package com.fenix.spirometer.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.model.City;
import com.fenix.spirometer.model.County;
import com.fenix.spirometer.model.Province;
import com.fenix.spirometer.room.database.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddrRepository {
    private static volatile AddrRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor;

    public AddrRepository() {
        database = AppDatabase.getInstance();
        executor = Executors.newCachedThreadPool();
    }

    public static AddrRepository getInstance() {
        if (instance == null) {
            instance = new AddrRepository();
        }
        return instance;
    }

    public LiveData<List<Province>> loadProvinces() {
        return database.provinceDao().getAllProvinces();
    }

    public MutableLiveData<List<City>> loadCities(String proUid) {
        final MutableLiveData<List<City>> mdCities = new MutableLiveData<>();
        executor.execute(() -> {
            mdCities.postValue(database.cityDao().getAllCities());
        });
        return mdCities;
    }

    public MutableLiveData<List<County>> loadCounties(String citUid) {
        final MutableLiveData<List<County>> mdCounties = new MutableLiveData<>();
        executor.execute(() -> {
            mdCounties.postValue(database.countyDao().getCounties(citUid));
        });
        return mdCounties;
    }

    public void initAddress(List<Province> provinces) {
        executor.execute(() -> {
            database.provinceDao().insertProvinces(provinces);
            for (Province model : provinces) {
                List<City> cities = model.getCities();
                database.cityDao().insertCities(cities);
                for (City city : cities) {
                    database.countyDao().insertCounties(city.getCounties());
                }
            }
//            loadProvinces();
//            loadCities(provinceModels.get(0).getUid());
//            loadCounties(provinceModels.get(0).getCities().get(0).getUid());
        });
    }

    public MutableLiveData<List<Province>> loadAll() {
        final MutableLiveData<List<Province>> mdProvinces = new MutableLiveData<>();
        executor.execute(() -> {
            List<Province> provinces = database.provinceDao().getAllProvinces();
            for (Province province : provinces) {
                List<City> cities = database.cityDao().getCities(province.getUid());
                for (City city : cities) {
                    city.setCounties(database.countyDao().getCounties(city.getUid()));
//                    Log.d("hff", provinceModel + ">>>>>" + cityModels + ">>>> = " + database.countyDao().getCounties(cityModel.getUid()));
                }
                province.setCities(cities);
            }
            mdProvinces.postValue(provinces);
        });
        return mdProvinces;
    }
}
