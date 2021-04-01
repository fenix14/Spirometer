package com.fenix.spirometer.ui.test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Stack<MeasureData> measureDataStack = new Stack<MeasureData>();

    // 测试开始时间
    private long startTimeStamp;
    // TODO：无数据时绘制0，待定。
    private static final int[] DEFAULT_DATA = new int[100];
    // 所有读出数据，不包括DEFAULT_DATA
    private List<MeasureData> measureDataList = new ArrayList<>();

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

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        if (btmNav != null) {
            btmNav.setVisibility(View.VISIBLE);
            btmNav.setText(R.string.footer_stop_testing);
            btmNav.setOnClickListener(this);
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

        rootView.findViewById(R.id.add).setOnClickListener(this);
        rootView.findViewById(R.id.add1).setOnClickListener(this);
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
    }

    @Override
    protected void initData() {
        Arrays.fill(DEFAULT_DATA, 0);
        chart1.animateX(1500);
        chart2.animateX(1500);

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
    }

    private void addEntry(LineChart chart, String title, int color, int[] data) {
        LineData lineData = chart.getData();
        if (lineData == null) {
            lineData = new LineData();
            chart.setData(lineData);
            chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
        }
//        ILineDataSet lastSet = lineData.getDataSetByIndex(lineData.getDataSetCount() - 1);
        // set.addEntry(...); // can be called as well
//        if (lastSet == null) {
//            lastSet = createSet(title, color);
//            lineData.addDataSet(lastSet);
//        }
        // choose a random dataSet
//        Entry entry = new Entry(lastSet.getEntryCount(), ((float) Math.random() * 50));
//        Log.d("hff", "entry = " + entry + ", count = " + (lineData.getDataSetCount() - 1));
//        lineData.addEntry(entry, lineData.getDataSetCount() - 1);
        lineData.addDataSet(createSet(title, color, data));
        chart.setVisibleXRangeMaximum(6000);
        chart.setVisibleXRangeMinimum(6000);
        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        chart.invalidate();
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//      // this automatically refreshes the chart (calls invalidate())
        //chart.moveViewTo(data.getEntryCount() - 7, 20f, YAxis.AxisDependency.LEFT);
    }

    int j = 0;

    private LineDataSet createSet(String title, int color, int[] data) {
        LineDataSet set = new LineDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //set.setColor(ColorTemplate.getHoloBlue());
        set.setColor(color);
        set.setLineWidth(2f);
       // set.setDrawIcons(false);
        //set.setDrawValues(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        for (int datum : data) {
            set.addEntry(new Entry(j++, datum));
        }
        return set;
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

    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

//        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex())
//                .getAxisDependency(), 500);
        //chart.zoomAndCenterAnimated(2.5f, 2.5f, e.getX(), e.getY(), chart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
        //chart.zoomAndCenterAnimated(1.8f, 1.8f, e.getX(), e.getY(), chart.getData().getDataSetByIndex(dataSetIndex)
        // .getAxisDependency(), 1000);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                testViewModel.startMeasure();
                break;
            case R.id.add1:
                chart1.clear();
//                addEntry(chart2, "流量容积杯", Color.rgb(240, 99, 99));
                testViewModel.stopMeasure();
                break;
            case R.id.footer:
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.BUNDLE_KEY_TIME_STAMP, startTimeStamp);
                NavHostFragment.findNavController(this).navigate(R.id.testing_to_result, bundle);
                break;
            default:
                break;
        }
    }

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MeasureData measureData = null;
            if (dataQueue.isEmpty()) {
                measureData = dataQueue.poll();
                measureDataList.add(measureData);
            }
            addEntry(chart1, "通气功能", ColorTemplate.getHoloBlue(), measureData == null ? DEFAULT_DATA : dataQueue.poll().voltages);
        }
    };

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void feedMultiple() {
        executorService.execute(() -> {
            for (int i = 0; i <= 60; i++) {
                // Don't generate garbage runnables inside the loop.
                try {
                    Thread.sleep(1000);
                    Activity activity = getActivity();
                    if (activity != null) {
                        requireActivity().runOnUiThread(runnable);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        for (int i = 0; i < 5; i++) {
//            initEntry(chart1, "通气功能", ColorTemplate.getHoloBlue());
//        }
    }

    public void initEntry(LineChart chart, String title, int color) {
//        LineData data = chart.getData();
//        if (data == null) {
//            data = new LineData();
//            chart.setData(data);
//            chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
//        }
//        ILineDataSet set = data.getDataSetByIndex(0);
//        // set.addEntry(...); // can be called as well
//        if (set == null) {
//            set = createSet(title, color);
//            data.addDataSet(set);
//        }
//        // choose a random dataSet
//        int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
//        ILineDataSet randomSet = data.getDataSetByIndex(randomDataSetIndex);
//        float value = (float) (Math.random() * 20) + 20f * (randomDataSetIndex + 1);
//        data.addEntry(new Entry(randomSet.getEntryCount(), 50f), randomDataSetIndex);
//        data.notifyDataChanged();
//        // let the chart know it's data has changed
//        chart.notifyDataSetChanged();
//        chart.setVisibleXRangeMaximum(200);
//        chart.setVisibleXRangeMinimum(200);
//
//       // chart.moveViewTo(data.getEntryCount() - 7, 20f, YAxis.AxisDependency.LEFT);
    }

    @Override
    protected void initObserver() {
        testViewModel.getBleDeviceState().observe(this, bleDeviceState -> {
            //TODO 蓝牙断开处理
            if (bleDeviceState == null) {

            } else if (bleDeviceState.getState() == BleDeviceState.State.STATE_TESTING) {
                startTimeStamp = System.currentTimeMillis();
                feedMultiple();
            } else if (bleDeviceState.getState() == BleDeviceState.State.STATE_FINISHED) {
                executorService.shutdownNow();
                String voltages = JSONUtils.modelList2Json(measureDataList);
                TestReport testReport = new TestReport(startTimeStamp, viewModel.getChosenMember(), voltages, viewModel.getLoginState().getValue().getLoginOperator(), 0);
                testViewModel.insertReport(testReport);
            }
        });

        testViewModel.getMeasureData().observe(this, dataQueue::offer);
    }
}
