package com.fenix.spirometer.util;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.ui.login.LoginViewModel;
import com.fenix.spirometer.ui.main.MainViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class AllViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(MyApplication.getInstance());
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}