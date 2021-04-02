package com.fenix.spirometer.ui.test;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.OverScroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.core.SmartTable;
import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.room.model.History;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.history.HistoryFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestReportFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener, View.OnClickListener {
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.00%");
    private TestReport testReport;
    private TestViewModel testViewModel;
    private SmartTable<TestParam> smartTable;
    private static final List<TestParam> PARAM_LIST = new ArrayList<>();
    private TextView tvMemName, tvMemGender, tvMemHeight, tvMemAge, tvMemWeight;
    private TextView tvLungsAge, tvActionStandardLvl, tvTestingDate, tvOperatorName;

    static {
        PARAM_LIST.add(new TestParam("FVC", 4.31f, 3.28f));
        PARAM_LIST.add(new TestParam("FEV", 3.59f, 3.20f));
        PARAM_LIST.add(new TestParam("FEV1/fVC", 0.8120f, 0.9769f));
        PARAM_LIST.add(new TestParam("PEF", 9.44f, 6.01f));
        PARAM_LIST.add(new TestParam("MEF75", 8.07f, 4.59f));
        PARAM_LIST.add(new TestParam("MEF50", 4.43f, 5.90f));
        PARAM_LIST.add(new TestParam("MEF25", 1.87f, 3.02f));
        PARAM_LIST.add(new TestParam("MMEF75/25", 3.66f, 4.33f));
        PARAM_LIST.add(new TestParam("EV%FVC", 0.05f, 0.0166f));
    }

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
        Button btmNav = getFooter();
        if (btmNav != null) {
            btmNav.setVisibility(View.VISIBLE);
            btmNav.setText(R.string.footer_print_report);
            btmNav.setOnClickListener(this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_test_report;
    }

    @Override
    protected void initView(View rootView) {
        testViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(TestViewModel.class);
        smartTable = rootView.findViewById(R.id.table);
        smartTable.setOverScrollMode(View.OVER_SCROLL_NEVER);
        smartTable.getConfig().setShowYSequence(false).setShowXSequence(false);

        tvMemName = rootView.findViewById(R.id.tv_name);
        tvMemGender = rootView.findViewById(R.id.tv_gender);
        tvMemHeight = rootView.findViewById(R.id.tv_height);
        tvMemAge = rootView.findViewById(R.id.tv_age);
        tvMemWeight = rootView.findViewById(R.id.tv_weight);
        tvLungsAge = rootView.findViewById(R.id.tv_lungs_age);
        tvActionStandardLvl = rootView.findViewById(R.id.tv_action_standard_level);
        tvTestingDate = rootView.findViewById(R.id.tv_testing_date);
        tvOperatorName = rootView.findViewById(R.id.tv_operator_name);
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
        smartTable.setData(PARAM_LIST);
    }

    private void fillViewsWithData(TestReport testReport) {
        Log.d("hff", "testReport: " + testReport);
        if (testReport == null) {
            Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        Member member = testReport.getMember();
        tvMemName.setText(String.format(getString(R.string.report_mem_name), member.getName()));
        tvMemGender.setText(String.format(getString(R.string.report_mem_gender), member.getGender()));
        tvMemHeight.setText(String.format(getString(R.string.report_mem_height), member.getHeight()));
        tvMemAge.setText(String.format(getString(R.string.report_mem_age), member.getAge()));
        tvMemWeight.setText(String.format(getString(R.string.report_mem_weight), member.getWeight()));
        tvLungsAge.setText(String.format(getString(R.string.report_lungs_age), 0.0f));
        tvActionStandardLvl.setText(String.format(getString(R.string.report_action_standard_lvl), "中"));
        tvTestingDate.setText(String.format(getString(R.string.report_testing_date), Utils.getDateByMills(testReport.getTimeMills())));
        tvOperatorName.setText(String.format(getString(R.string.report_operator_name), testReport.getOperator().getDisplayName()));
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        NavHostFragment.findNavController(this).navigate(R.id.frag_history, getArguments());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footer) {
            // TODO: 跳转到打印
        }
    }

    @com.bin.david.form.annotation.SmartTable
    static class TestParam {
        @SmartColumn(id = 1, name = "参数")
        public String name;
        @SmartColumn(id = 2, name = "预测值")
        public float predict;
        @SmartColumn(id = 3, name = "实际值")
        public float actual;
        @SmartColumn(id = 4, name = "实/预")
        public String percent;

        TestParam(String name, float predict, float actual) {
            this.name = name;
            this.predict = predict;
            this.actual = actual;
            this.percent = PERCENT_FORMAT.format(actual / predict);
        }
    }
}
