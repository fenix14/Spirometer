package com.fenix.spirometer.ui.test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ble.MeasureData;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.model.TestReport;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.JSONUtils;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestingFragment extends BaseVMFragment implements View.OnClickListener, OnChartValueSelectedListener {
    private final static String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    private final static String[] parties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    private ConcurrentLinkedQueue<MeasureData> dataQueue = new ConcurrentLinkedQueue<>();
    private TestViewModel testViewModel;
    private LineChart chart1, chart2;
    private Typeface tfRegular, tfLight;

    // 所有读出数据
    private List<MeasureData> measureDataList = new ArrayList<>();

    private Timer timer;
    private View innerFooter;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.clear();
            toolbar.setCenterText(getString(R.string.tab_testing));
            toolbar.setLeftText(null);
            toolbar.setRightText(null);
        }
        initFooter(true);
    }

    private void initFooter(boolean isTesting) {
        viewModel.setShowNavBar(false);
        Button footer = getFooter();
        if (isTesting) {
            if (footer != null) {
                footer.setVisibility(View.VISIBLE);
                innerFooter.setVisibility(View.GONE);
                footer.setText(R.string.footer_stop_testing);
                footer.setOnClickListener(this);
            }
        } else {
            innerFooter.setVisibility(View.VISIBLE);
            if (footer != null) {
                footer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tfRegular = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_linechart;
    }

    @Override
    protected void initView(View rootView) {
        testViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(TestViewModel.class);

        chart1 = rootView.findViewById(R.id.chart1);
        chart2 = rootView.findViewById(R.id.chart2);
        chart1.setOnChartValueSelectedListener(this);
        chart2.setOnChartValueSelectedListener(this);

        // no description text
        chart1.getDescription().setEnabled(false);
        chart2.getDescription().setEnabled(false);

        // enable touch gestures
        chart1.setTouchEnabled(false);
        chart2.setTouchEnabled(true);
        chart1.setDragDecelerationFrictionCoef(0.9f);
        chart2.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart1.setDragEnabled(true);
        chart2.setDragEnabled(true);
        chart1.setScaleEnabled(true);
        chart2.setScaleEnabled(true);
        chart1.setDrawGridBackground(false);
        chart2.setDrawGridBackground(false);
        chart1.setHighlightPerDragEnabled(true);
        chart2.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart1.setPinchZoom(true);
        chart2.setPinchZoom(true);

        // set an alternative background color
        chart1.setBackgroundColor(Color.LTGRAY);
        chart2.setBackgroundColor(Color.LTGRAY);

        YAxis rightAxis = chart1.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis rightAxis2 = chart2.getAxisRight();
        rightAxis2.setEnabled(false);

        innerFooter = rootView.findViewById(R.id.finish_footer);
        rootView.findViewById(R.id.restart).setOnClickListener(this);
        rootView.findViewById(R.id.report).setOnClickListener(this);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        chart2.animateX(1500);
        Legend mLegend1=chart1.getLegend();
        mLegend1.setEnabled(false);

        ValueFormatter xAxisFormatter = new CustomFormatter(0);
        XAxis xAxis = chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        // xAxis.setLabelCount(15);
        xAxis.setValueFormatter(xAxisFormatter);
        //xAxis.setLabelCount(5, true);
        xAxis.setGranularityEnabled(true);


        XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter);
        mv.setChartView(chart1); // For bounds control
        chart1.setMarker(mv);

        feedMultiple();
    }

    private void addEntry(LineChart chart, String title, int color, int[] data) {
        LineData lineData = chart.getData();
        if (lineData == null) {
            lineData = new LineData();
            chart.setData(lineData);
            chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
        }
        Log.d("hff", "entry = " + data);
        lineData.addDataSet(createSet(title, color, data));
        chart.setVisibleXRangeMaximum(6000);
        chart.setVisibleXRangeMinimum(6000);
        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    int j = 0;
    private LineDataSet createSet(String title, int color, int[] data) {
        LineDataSet set = new LineDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(2f);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        for (int datum : data) {
            set.addEntry(new Entry(j++, datum));
        }
        return set;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.footer:
                testViewModel.stopMeasure();
                break;
            case R.id.restart:
                initFooter(true);
                chart1.clear();
                testViewModel.startMeasure();
                break;
            case R.id.report:
                //TODO:图
                if (measureDataList.isEmpty()) {
                    Toast.makeText(requireContext(), "数据不足，无法生成报告", Toast.LENGTH_SHORT).show();
                } else {
                    testViewModel.insertReport(createTestReport()).observe(this, insertId -> {
                        if (insertId > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(Constants.BUNDLE_KEY_TIME_STAMP, measureDataList.get(0).timeStamp);
                            NavHostFragment.findNavController(this).navigate(R.id.testing_to_result, bundle);
                        }
                    });
                }
                break;
            case R.id.cancel:
                NavHostFragment.findNavController(this).navigate(R.id.frag_home);
                break;
            default:
                break;
        }
    }

    private void feedMultiple() {
        testViewModel.startMeasure();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity != null) {
                    Log.d("hff", "TimerTask");
                    requireActivity().runOnUiThread(runnable);
                }
            }
        }, 1000, 200);
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!dataQueue.isEmpty()) {
                MeasureData measureData = dataQueue.poll();
                Log.d("hff", "rec: measureData: " + Arrays.toString(measureData.flow));
                if (measureData.flow == null) {
                    Log.d("hff", "test finish");
                    // 结束时会发送空数据
                    if (timer != null) {
                        timer.cancel();
                    }
                    initFooter(false);
                    return;
                }
                measureDataList.add(measureData);
                addEntry(chart1, "通气功能", ColorTemplate.getHoloBlue(), measureData.flow);
            }
        }
    };

    private TestReport createTestReport() {
        float fvc = 0; // 最大呼气容积
        float fev1 = 0; // 第1秒呼气容积，20ms/点，Math.sum(50个点，不足50计单程)
        float pef = 0;  // 呼气峰值流量
        float mvv = 0;
        float vc = 0;
        float tlc = 0; //最大吸气容积

        float tempFvcSum = 0;
        float tempFev1Sum = 0;
        int inhaleCumCount = 0; // 吸气数据个数
        int exhaleCumCount = 0; // 呼气数据个数
        float tempTlcSum = 0;
        float preFlow = 0;

        long timeStampStart = 0;
        long timeStampStop = 0;
        for (MeasureData data : measureDataList) {
            int[] flows = data.flow;
            for (int i = 0; i < flows.length; i++) {
                int flow = flows[i];
                if (flow > 0) {
                    // 找呼气流量峰值PEF
                    pef = Math.max(pef, flow);
                    if (preFlow > 0) {
                        // 继续呼气
                        if (data.timeStamp + 20 * i - timeStampStart <= 1000) {
                            // 还在第一秒，累计FEV1
                            tempFev1Sum += flow;
                        } else if (tempFev1Sum > 0) {
                            // 结算(FEV1 = 平均流量SLM * 1秒) TODO：确认M是否分钟
                            fev1 = Math.max(fev1, (tempFev1Sum / exhaleCumCount) / 60);
                            tempFev1Sum = 0;
                        }

                        timeStampStop = data.timeStamp + i * 20;
                    } else {
                        // 开始一次新的呼气，结算前一次吸气的数据
                        if (tempTlcSum < 0 && timeStampStop > 0) {
                            // 结算(TLC = 平均流量SLM * 分钟数) TODO：确认M是否分钟
                            tlc = Math.min(tlc, (tempTlcSum / inhaleCumCount) * (timeStampStop - timeStampStart) / 60000);
                        }
                        timeStampStart = data.timeStamp + i * 20;
                        tempFev1Sum += flow;
                        inhaleCumCount = 0;
                    }
                    //累计FVC
                    tempFvcSum += flow;
                    exhaleCumCount++;
                } else if (flow < 0) {
                    if (preFlow < 0) {
                        // 继续吸气
                        timeStampStop = data.timeStamp + i * 20;
                    } else {
                        // 开始一次新的吸气，结算前一次呼气FVC
                        if (tempFvcSum > 0 && timeStampStop > 0) {
                            // 结算(FVC = 平均流量SLM * 分钟数) TODO：确认M是否分钟
                            float exhaleFlow = (tempFvcSum / exhaleCumCount) * (timeStampStop - timeStampStart) / 60000;
                            fvc = Math.max(fvc, exhaleFlow);
                            // 如果前一次呼气未超过1秒，结算其FEV1
                            if (tempFev1Sum > 0.01f) {
                                fev1 = Math.max(fev1, exhaleFlow);
                                tempFev1Sum = 0;
                            }
                        }

                        timeStampStart = data.timeStamp + i * 20;
                        exhaleCumCount = 0;
                    }
                    //累计TLC
                    tempTlcSum += flow;
                    inhaleCumCount++;
                } else {

                    continue;
                }
                preFlow = flow;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("result: fvc = " + fvc)
                .append(", fev1 = " + fev1)
                .append(", pef = " + pef)
                .append(" ,tlc = " + tlc)
                .append(" ,mvv = " + mvv);
        mvv = fev1 * 35;
        Log.d("hff", sb.toString());

        String voltages = JSONUtils.modelList2Json(measureDataList);
        return new TestReport(measureDataList.get(0).timeStamp,
                viewModel.getChosenMember(), voltages,
                viewModel.getLoginState().getValue().getLoginOperator(), 0, fvc, fev1, pef, mvv, Math.abs(tlc), vc);
    }

    @Override
    protected void initObserver() {
        testViewModel.getBleDeviceState().observe(this, bleDeviceState -> {
            if (bleDeviceState == null || (bleDeviceState.getState() == BleDeviceState.State.STATE_DISCONNECTED)) {
                Toast.makeText(getContext(), "蓝牙断开，请重新连接", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
            }
        });

        testViewModel.getMeasureData().observe(this, dataQueue::offer);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    private void saveToGallery() {
        saveToGallery(chart1, "LineChartActivity2");
        saveToGallery(chart2, "LineChartActivity2");
    }

    private void saveToGallery(Chart chart, String name) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            Toast.makeText(requireContext(), "Saving SUCCESSFUL!",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(requireContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                    .show();
    }
}
