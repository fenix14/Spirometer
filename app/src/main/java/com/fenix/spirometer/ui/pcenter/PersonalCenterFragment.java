package com.fenix.spirometer.ui.pcenter;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseToolbarFragment;

public class PersonalCenterFragment extends BaseToolbarFragment {

    private PersonalCenterViewModel mViewModel;

    public static PersonalCenterFragment newInstance() {
        return new PersonalCenterFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_personal;
    }

    @Override
    protected void initView(View rootView) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PersonalCenterViewModel.class);
        // TODO: Use the ViewModel
    }

}