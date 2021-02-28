package com.fenix.spirometer.ui.memberlist;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseToolbarFragment;

public class MemberListFragment extends BaseToolbarFragment {

    private MemberListViewModel mViewModel;

    public static MemberListFragment newInstance() {
        return new MemberListFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_members;
    }
    @Override
    protected void initView(View rootView) {

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MemberListViewModel.class);
        // TODO: Use the ViewModel
    }

}