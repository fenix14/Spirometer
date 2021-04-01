package com.fenix.spirometer.ui.test;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.room.model.History;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.history.HistoryFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Constants;

public class TestReportFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener {
    private TestReport testReport;
    private TestViewModel testViewModel;

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
        testViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(TestViewModel.class);
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            NavHostFragment.findNavController(this).navigate(R.id.frag_home);
            Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        testViewModel.getReport(bundle.getLong(Constants.BUNDLE_KEY_TIME_STAMP, 0L)).observe(this, this::fillViewsWithData);
    }

    private void fillViewsWithData(TestReport testReport) {
        if (testReport == null) {
            Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        NavHostFragment.findNavController(this).navigate(R.id.frag_history, getArguments());
    }

    private class Param {
        public String name;
        public float predict;
        public float actual;
    }
}
