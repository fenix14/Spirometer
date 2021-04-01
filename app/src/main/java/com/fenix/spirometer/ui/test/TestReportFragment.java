package com.fenix.spirometer.ui.test;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.room.model.History;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.history.HistoryFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;

public class TestReportFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener {
    private SimpleReport simpleReport;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(R.string.tab_report));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(viewModel.isTesting() ? null : getString(R.string.btn_compare));
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_test_report;
    }

    @Override
    protected void initView(View rootView) {

    }

    @Override
    protected void initData() {
//        Bundle bundle = getArguments();
//        if (bundle == null || !(bundle.get(HistoryFragment.KEY_REPORT) instanceof SimpleReport)) {
//            NavHostFragment.findNavController(this).navigateUp();
//            return;
//        }
//        simpleReport = (SimpleReport) bundle.get(HistoryFragment.KEY_REPORT);
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        NavHostFragment.findNavController(this).navigate(R.id.frag_history, getArguments());
    }
}
