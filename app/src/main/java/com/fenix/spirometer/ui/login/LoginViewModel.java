package com.fenix.spirometer.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.model.Administrator;
import com.fenix.spirometer.room.AdminRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * VM：登录页面
 */
public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<InputDataState> inputDataState = new MutableLiveData<>();
    private MutableLiveData<Administrator> currentAdmin;
    private AdminRepository adminRepository;
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        adminRepository = new AdminRepository();
    }

    LiveData<InputDataState> getInputDataState() {
        return inputDataState;
    }

    public void login(String username, String password) {
        cachedThreadPool.execute(() -> currentAdmin = adminRepository.getAdministrator(username, password));
    }

    public MutableLiveData<Administrator> getAdministrator() {
        return currentAdmin;
    }

    public void inputDataChanged(String username, String password) {
        inputDataState.setValue(new InputDataState(isUserNameValid(username), isPasswordValid(password)));
    }

    private boolean isUserNameValid(String username) {
        return Constants.USER_NAME_PATTERN.matcher(username).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > Constants.PWD_LENGTH_MIN;
    }
}