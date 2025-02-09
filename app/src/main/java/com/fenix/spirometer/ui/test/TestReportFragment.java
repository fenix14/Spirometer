package com.fenix.spirometer.ui.test;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
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
import com.fenix.spirometer.ble.DataUtils;
import com.fenix.spirometer.ble.MeasureData;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.print.SunmiPrintHelper;
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
    private TextView tvMemName, tvMemGender, tvMemHeight, tvMemAge, tvMemWeight;
    private TextView tvLungsAge, tvActionStandardLvl, tvTestingDate, tvOperatorName;
    private ImageView imageView1, imageView2;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_test_report;
    }

    @Override
    protected void initToolNavBar() {
        if (viewModel.isTesting()) {
            NavHostFragment.findNavController(this).getCurrentDestination().setLabel("toHome");
        }

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
        smartTable.getConfig().setHorizontalPadding(1);
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
        testViewModel.getReport(bundle.getLong(Constants.BUNDLE_KEY_TIME_STAMP, 0L)).observe(this, testReport -> {
            if (testReport != null) {
                decodeTestData(testReport);
            }
        });
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
            print();
        }
    }

    /**
     * Scaled image width is an integer multiple of 8 and can be ignored
     */
    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        Log.d("wuxin", "width0==" + width + "==height1" + height);
        // 设置想要的大小
        int newWidth = (width / 14 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        //Log.d("wuxin","width=="+width+"==height"+height);
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }

    private void print() {
        //标题
        SunmiPrintHelper.getInstance().setAlign(1);
        SunmiPrintHelper.getInstance().printText("肺活量标准测试报告\n", 32f, false, false);
        //人员信息
        SunmiPrintHelper.getInstance().setAlign(0);
        SunmiPrintHelper.getInstance().printTable(new String[]{tvMemName.getText().toString(), tvMemGender.getText().toString()},
                new int[]{1, 1}, new int[]{0, 0});
        SunmiPrintHelper.getInstance().printTable(new String[]{tvMemHeight.getText().toString(), tvMemAge.getText().toString(), tvMemWeight.getText().toString()},
                new int[]{1, 1, 1}, new int[]{0, 1, 2});
        SunmiPrintHelper.getInstance().printText("  \n", 22f, false, false);
        // TODO: 跳转到打印
        //图片
        Bitmap bitmap = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_image, null);
        }
        bitmap = scaleImage(bitmap);
        SunmiPrintHelper.getInstance().setAlign(0);
        SunmiPrintHelper.getInstance().printBitmap(scaleImage(bitmap), 1);
        SunmiPrintHelper.getInstance().printText("  \n", 22f, false, false);
        //表格
        int[] width = new int[]{3, 2, 2, 3};
        int[] align = new int[]{0, 1, 1, 2};
        List<TestParam> params = smartTable.getTableData().getT();
        SunmiPrintHelper.getInstance().setAlign(0);
        SunmiPrintHelper.getInstance().printTable(TestParam.getTitle(),
                width, align);
        for (TestParam tableItem : params) {
            SunmiPrintHelper.getInstance().printTable(tableItem.getLineValue(),
                    width, align);
        }
        SunmiPrintHelper.getInstance().printText("", 22f, false, false);
        SunmiPrintHelper.getInstance().setAlign(0);
        SunmiPrintHelper.getInstance().printText(tvLungsAge.getText() + "\n",
                22f, false, false);
        SunmiPrintHelper.getInstance().printText(tvActionStandardLvl.getText() + "\n",
                22f, false, false);
        SunmiPrintHelper.getInstance().printText(tvTestingDate.getText() + "\n",
                22f, false, false);
        SunmiPrintHelper.getInstance().printText(tvOperatorName.getText() + "\n",
                22f, false, false);
        SunmiPrintHelper.getInstance().feedPaper();
    }

    /**
     * 解析测试数据，生成加载数据和显示图片
     */
    private void decodeTestData(TestReport testReport) {
        if (testReport == null) {
            return;
        }
        new Thread(() -> {
            // 填充TextView
            setTextResources(testReport);

            // TODO: 获取图片资源
            MeasureData measureData = JSONUtils.json2Model(testReport.getData(), MeasureData.class);
            if (measureData == null) {
                return;
            }

            // 填充ImageView
            setImageResource(0, getBitmapFromRawData(measureData.flow));
            setImageResource(1, getBitmapFromRawData(measureData.flow));
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
            if (member != null) {
                tvMemName.setText(getString(R.string.report_mem_name, member.getName()));
                tvMemGender.setText(getString(R.string.report_mem_gender, member.getGender()));
                tvMemHeight.setText(getString(R.string.report_mem_height, member.getHeight()));
                tvMemAge.setText(getString(R.string.report_mem_age, member.getAge()));
                tvMemWeight.setText(getString(R.string.report_mem_weight, member.getWeight()));
            }
            Operator operator = testReport.getOperator();
            if (operator != null) {
                tvOperatorName.setText(getString(R.string.report_operator_name, operator.getDisplayName()));
            }
            tvLungsAge.setText(getString(R.string.report_lungs_age, String.valueOf(0.0f)));
            tvActionStandardLvl.setText(getString(R.string.report_action_standard_lvl, "中"));
            tvTestingDate.setText(getString(R.string.report_testing_date, Utils.getDateByMills(testReport.getTimeMills())));

            List<TestParam> paramList = new ArrayList<>();
            paramList.add(new TestParam("FVC", 4.31f, DataUtils.getShorterFloat(testReport.getFVC(), 2)));
            paramList.add(new TestParam("FEV1", 3.59f, DataUtils.getShorterFloat(testReport.getFEV1(), 2)));
            float fev1pFvc = testReport.getFVC() < 0.01 ? 0 : DataUtils.getShorterFloat(testReport.getFEV1() / testReport.getFVC(), 2);
            paramList.add(new TestParam("FEV1/fVC", 0.8120f, fev1pFvc));
            paramList.add(new TestParam("PEF", 9.44f, DataUtils.getShorterFloat(testReport.getPEF(), 2)));
            paramList.add(new TestParam("MVV", 125.65f, DataUtils.getShorterFloat(testReport.getMVV(), 2)));
            paramList.add(new TestParam("TLC", 4.31f, DataUtils.getShorterFloat(testReport.getTLC(), 2)));
            paramList.add(new TestParam("VC", 1f, DataUtils.getShorterFloat(testReport.getFVC(), 2)));
            smartTable.setData(paramList);
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

        public String[] getLineValue() {
            return new String[]{name, String.valueOf(predict), String.valueOf(actual), percent};
        }

        public static String[] getTitle(){
            return new String[]{"指标","Pred","BEST","%Pred"};
        }
    }
}
