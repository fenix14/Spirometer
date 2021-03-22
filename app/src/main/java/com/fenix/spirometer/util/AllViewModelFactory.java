package com.fenix.spirometer.util;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.fenix.spirometer.ui.login.LoginViewModel;
import com.fenix.spirometer.ui.main.MainViewModel;
import com.fenix.spirometer.ui.member.MemberViewModel;
import com.fenix.spirometer.ui.pcenter.detectorcalibration.DetectorCalibViewModel;
import com.fenix.spirometer.ui.pcenter.estvaluelist.EditEstValueViewModel;
import com.fenix.spirometer.ui.pcenter.estvaluelist.EstiValueViewModel;
import com.fenix.spirometer.ui.pcenter.operatorlist.OperatorViewModel;
import com.fenix.spirometer.ui.test.TestViewModel;

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
            return (T) new LoginViewModel();
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel();
        } else if (modelClass.isAssignableFrom(MemberViewModel.class)) {
            return (T) new MemberViewModel();
        } else if (modelClass.isAssignableFrom(OperatorViewModel.class)) {
            return (T) new OperatorViewModel();
        } else if (modelClass.isAssignableFrom(EstiValueViewModel.class)) {
            return (T) new EstiValueViewModel();
        } else if (modelClass.isAssignableFrom(EditEstValueViewModel.class)) {
            return (T) new EditEstValueViewModel();
        } else if (modelClass.isAssignableFrom(DetectorCalibViewModel.class)) {
            return (T) new DetectorCalibViewModel();
        } else if (modelClass.isAssignableFrom(TestViewModel.class)) {
            return (T) new TestViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}