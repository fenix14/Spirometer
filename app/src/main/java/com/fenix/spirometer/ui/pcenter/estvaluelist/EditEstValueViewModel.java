package com.fenix.spirometer.ui.pcenter.estvaluelist;

import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.room.EstValueRepository;

public class EditEstValueViewModel extends ViewModel {
    private final EstValueRepository estValueRepo;

    public EditEstValueViewModel() {
        estValueRepo = new EstValueRepository();
    }

    public void updateEstValue(EstValue estValue) {
        estValueRepo.updateEstiValue(estValue);
    }
}
