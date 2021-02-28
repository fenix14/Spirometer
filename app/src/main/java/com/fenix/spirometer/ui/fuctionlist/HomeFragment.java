package com.fenix.spirometer.ui.fuctionlist;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseToolbarFragment;

public class HomeFragment extends BaseToolbarFragment {


    private FunctionListViewModel mViewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_home;
    }

    @Override
    protected void initView(View rootView) {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FunctionListViewModel.class);
        // TODO: Use the ViewModel
    }

}