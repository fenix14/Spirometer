package com.fenix.spirometer.room;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.model.Administrator;
import com.fenix.spirometer.room.bean.AdminModel;
import com.fenix.spirometer.room.dao.AdminDao;
import com.fenix.spirometer.room.database.AppDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理员信息仓库入口
 */
public class AdminRepository {
    private static volatile AdminRepository instance;
    private final AppDatabase database;
    private final AdminDao adminDao;
    private final ExecutorService executor;
    private MutableLiveData<List<AdminModel>> users;
    private MutableLiveData<Administrator> adminCache;

    public AdminRepository() {
        database = AppDatabase.getInstance();
        adminDao = database.adminDao();
        executor = Executors.newCachedThreadPool();
        adminDao.insert(new AdminModel("18674018759", "贺冯飞", "123456", "啥也不是"));
    }

    public static AdminRepository getInstance() {
        if (instance == null) {
            instance = new AdminRepository();
        }
        return instance;
    }

    public MutableLiveData<Administrator> getAdministrator(String adminId, String password) {
        if (adminCache == null) {
            adminCache = new MutableLiveData<>();
        }
        executor.execute(() -> {
            if (adminCache.getValue() == null || !adminCache.getValue().isSameAdmin(adminId, password)) {
                adminCache.postValue(model2Object(database.adminDao().getAdmin(adminId)));
            }
        });
        return adminCache;
    }

    public void insertAdministrator(Administrator... admins) {
        for (Administrator admin : admins) {
            database.adminDao().insert(object2Model(admin));
        }
    }

    private Administrator model2Object(AdminModel model) {
        return model == null ? null : new Administrator(model.getUserId(), model.getDisplayName(), model.getPassword(), model.getDuty());
    }

    private AdminModel object2Model(Administrator admin) {
        return admin == null ? null : new AdminModel(admin.getAdminId(), admin.getDisplayName(), admin.getPassword(), admin.getDuty());
    }
}
