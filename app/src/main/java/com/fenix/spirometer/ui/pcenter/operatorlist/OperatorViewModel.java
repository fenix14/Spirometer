package com.fenix.spirometer.ui.pcenter.operatorlist;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.room.OperatorRepository;

import java.util.List;

public class OperatorViewModel extends ViewModel {
    private OperatorRepository operRepo;

    public OperatorViewModel() {
        operRepo = new OperatorRepository();
    }

    public void insertOperator(Operator operator) {
        operRepo.insertOperator(operator);
    }

    public void subscribeToOperators(LifecycleOwner lifecycleOwner, Observer<List<Operator>> observer) {
        operRepo.loadAllOperators().observe(lifecycleOwner, observer);
    }

    public void deleteOperator(Operator operator) {
        operRepo.deleteOperator(operator);
    }
}