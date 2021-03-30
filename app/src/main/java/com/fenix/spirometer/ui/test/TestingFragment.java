package com.fenix.spirometer.ui.test;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ble.MeasureData;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.pcenter.detectorcalibration.DetectorCalibViewModel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

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
    private Handler handler = new Handler(Looper.getMainLooper());

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

    private void addEntry(LineChart chart, String title, int color) {
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
        lineData.addDataSet(createSet(title, color));
        chart.setVisibleXRangeMaximum(60000);
        chart.setVisibleXRangeMinimum(60000);
        // let the chart know it's data has changed
        chart.notifyDataSetChanged();
        chart.invalidate();
        //chart.setVisibleYRangeMaximum(15, AxisDependency.LEFT);
//      // this automatically refreshes the chart (calls invalidate())
        //chart.moveViewTo(data.getEntryCount() - 7, 20f, YAxis.AxisDependency.LEFT);
    }

    int j = 0;

    private LineDataSet createSet(String title, int color) {
        LineDataSet set = new LineDataSet(null, title);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //set.setColor(ColorTemplate.getHoloBlue());
        set.setColor(color);
        set.setLineWidth(2f);
        set.setDrawIcons(!set.isDrawIconsEnabled());
        set.setDrawValues(!set.isDrawValuesEnabled());
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawCircles(false);
        for (int k = 0; k < 100; k++) {
            set.addEntry(new Entry(j++, ((float) Math.random() * 50)));
        }
        Log.d("hff2", "after createSet: j = " + j);
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
                feedMultiple();
                break;
            case R.id.add1:
                addEntry(chart2, "流量容积杯", Color.rgb(240, 99, 99));
                break;
            default:
                NavHostFragment.findNavController(this).navigate(R.id.testing_to_result);
                break;
        }
    }


    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            addEntry(chart1, "通气功能", ColorTemplate.getHoloBlue());
        }
    };

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private void feedMultiple() {
        executorService.execute(() -> {
            for (int i = 0; i <= 600; i++) {
                // Don't generate garbage runnables inside the loop.
                try {
                    Log.d("hff1", "before sleep");
                    Thread.sleep(100);
                    Log.d("hff1", "after sleep");
                    requireActivity().runOnUiThread(runnable);
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
        });
        testViewModel.getMeasureData().observe(this, dataQueue::offer);
    }
}
