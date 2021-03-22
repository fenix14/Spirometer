package com.fenix.spirometer.ui.pcenter.estvaluelist;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.room.EstValueRepository;

import java.util.List;

public class EstiValueViewModel extends ViewModel {
    private final EstValueRepository estValueRepo;
    private LiveData<List<EstValue>> maleEstValues;
    private LiveData<List<EstValue>> femaleEstValues;
    private final MutableLiveData<List<EstValue>> currentEstValue = new MutableLiveData<>();
    private boolean isMale = true;

    public EstiValueViewModel() {
        estValueRepo = new EstValueRepository();
    }

    public void subscribeToEstValues(LifecycleOwner lifecycleOwner, Observer<List<EstValue>> observer) {
        if (maleEstValues == null) {
            maleEstValues = estValueRepo.getAllByGender(true);
            femaleEstValues = estValueRepo.getAllByGender(false);
        }
        maleEstValues.observe(lifecycleOwner, estValues -> pushData(estValues, true));
        femaleEstValues.observe(lifecycleOwner, estValues -> pushData(estValues, false));
        currentEstValue.observe(lifecycleOwner, observer);
    }

    public void loadEstValuesWithGender(boolean isMale) {
        this.isMale = isMale;
        if (maleEstValues == null) {
            return;
        }
        pushData((isMale ? maleEstValues : femaleEstValues).getValue(), isMale);
    }

    private void pushData(List<EstValue> estValues, boolean isMale) {
        if (this.isMale == isMale) {
            currentEstValue.postValue(estValues);
        }
    }
}
