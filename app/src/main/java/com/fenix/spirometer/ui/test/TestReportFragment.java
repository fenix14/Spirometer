package com.fenix.spirometer.ui.test;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.core.SmartTable;
import com.fenix.spirometer.R;
import com.fenix.spirometer.ble.MeasureData;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.JSONUtils;
import com.fenix.spirometer.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestReportFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener, View.OnClickListener {
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.00%");
    private TestViewModel testViewModel;
    private SmartTable<TestParam> smartTable;
    private static final List<TestParam> PARAM_LIST = new ArrayList<>();
    private TextView tvMemName, tvMemGender, tvMemHeight, tvMemAge, tvMemWeight;
    private TextView tvLungsAge, tvActionStandardLvl, tvTestingDate, tvOperatorName;
    private ImageView imageView1, imageView2;

    static {
        // TODO：测试用
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
    protected int getLayoutId() {
        return R.layout.frag_test_report;
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

        imageView1 = rootView.findViewById(R.id.iv_image1);
        imageView2 = rootView.findViewById(R.id.iv_image2);
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            NavHostFragment.findNavController(this).navigate(R.id.frag_home);
            Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        testViewModel.getReport(bundle.getLong(Constants.BUNDLE_KEY_TIME_STAMP, 0L)).observe(this, this::decodeTestData);
    }

    @Override
    public void onLeftClick() {
        if (viewModel.isTesting()) {
            NavHostFragment.findNavController(this).navigate(R.id.frag_home);
        } else {
            NavHostFragment.findNavController(this).navigateUp();
        }
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

    /**
     * 解析测试数据，生成加载数据和显示图片
     */
    private void decodeTestData(TestReport testReport) {
        new Thread(() -> {
            // 填充TextView
            setTextResources(testReport);

            // TODO: 获取图片资源
            MeasureData measureData = JSONUtils.json2Model(testReport.getData(), MeasureData.class);
            if (measureData == null) {
                return;
            }

            // 填充ImageView
            setImageResource(0, getBitmapFromRawData(measureData.voltages));
            setImageResource(1, getBitmapFromRawData(measureData.voltages));
        }).start();
    }

    private void setTextResources(TestReport testReport) {
        getActivity().runOnUiThread(() -> {
            Log.d("hff", "testReport: " + testReport);
            if (testReport == null) {
                Toast.makeText(getActivity(), "数据加载失败", Toast.LENGTH_SHORT).show();
                return;
            }

            Member member = testReport.getMember();
            tvMemName.append(member.getName());
            tvMemGender.append(member.getGender());
            tvMemHeight.append(member.getHeight() + "cm");
            tvMemAge.append(member.getAge() + "岁");
            tvMemWeight.append(member.getWeight() + "kg");
            tvLungsAge.append(String.valueOf(0.0f));
            tvActionStandardLvl.append("中");
            tvTestingDate.append(Utils.getDateByMills(testReport.getTimeMills()));
            tvOperatorName.append(testReport.getOperator().getDisplayName());

            //TODO: 数据转换为预计值、实际值、比值。
            smartTable.setData(PARAM_LIST);
        });
    }

    /**
     * 根据数据生成图片，也可能是从数据库？
     *
     * @param data 原始数据
     * @return 图片
     */
    private static Bitmap getBitmapFromRawData(int[] data) {
        // TODO
        return null;
    }

    private void setImageResource(int which, Bitmap bitmap) {
        getActivity().runOnUiThread(() -> {
            ImageView imageView = which == 0 ? imageView1 : imageView2;
            if (bitmap == null) {
                imageView.setImageResource(R.drawable.default_image);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * SmartTable表格每行参数封装
     */
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
