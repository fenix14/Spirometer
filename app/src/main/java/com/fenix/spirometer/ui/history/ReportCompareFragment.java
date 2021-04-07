package com.fenix.spirometer.ui.history;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.BaseModel;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.test.TestViewModel;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.ModelUtils;
import com.fenix.spirometer.util.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReportCompareFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener {
    private static final int COMPARE_ITEM_COUNT = 2;
    private TestViewModel testViewModel;
    private CustomExcel<TestTarget> excel;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(R.string.tab_report_compare));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        if (btmNav != null) {
            btmNav.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_report_compare;
    }

    @Override
    protected void initView(View rootView) {
        testViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(TestViewModel.class);
        excel = rootView.findViewById(R.id.customExcel);
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        long[] timeStamps = bundle.getLongArray(Constants.BUNDLE_KEY_TIME_STAMPS);
        if (timeStamps == null) {
            return;
        }
        testViewModel.getReports(timeStamps).observe(this, this::decodeTestData);
    }

    private void decodeTestData(List<TestReport> reportList) {
        if (reportList == null || reportList.size() != COMPARE_ITEM_COUNT) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            TestReport left = reportList.get(0);
            TestReport right = reportList.get(1);
            String[] headers = new String[]{getString(R.string.excel_title_target_name), left.getMember().getName(), right.getMember().getName()};
            List<Method> methodList = ModelUtils.getGetters(TestTarget.class, new String[]{"getTargetName", "getResult1", "getResult2"});
            Log.d("hff", "methodList = " + methodList);
            excel.setup(headers, new int[]{1, 1, 1}, new ArrayList<>(), methodList);
            excel.reload(getTestTargets(reportList));
        });
    }

    private List<TestTarget> getTestTargets(List<TestReport> testReports) {
        //TODO: 从测试数据中解析出指标值对
        List<TestTarget> testTargets = new ArrayList<>();
        testTargets.add(new TestTarget("指标一", "0", "0"));
        testTargets.add(new TestTarget("指标二", "0", "0"));
        testTargets.add(new TestTarget("指标三", "0", "0"));
        testTargets.add(new TestTarget("指标四", "0", "0"));
        testTargets.add(new TestTarget("指标五", "0", "0"));
        testTargets.add(new TestTarget("指标六", "0", "0"));
        return testTargets;
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }

    public static class TestTarget extends BaseModel {
        private String targetName;
        private String result1;
        private String result2;

        public TestTarget(String targetName, String result1, String result2) {
            this.targetName = targetName;
            this.result1 = result1;
            this.result2 = result2;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public String getResult1() {
            return result1;
        }

        public void setResult1(String result1) {
            this.result1 = result1;
        }

        public String getResult2() {
            return result2;
        }

        public void setResult2(String result2) {
            this.result2 = result2;
        }
    }
}
