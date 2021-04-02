package com.fenix.spirometer.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener, CustomExcel.OnRowStateChangeListener, View.OnClickListener {
    public static final String KEY_REPORT = "report";
    private CustomExcel<SimpleReport> excel;
    private boolean isEdit = false;
    private long chosenReportTimeStamp;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(R.string.tab_history));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        if (btmNav != null) {
            if (chosenReportTimeStamp > 0) {
                btmNav.setVisibility(View.VISIBLE);
                btmNav.setText(R.string.footer_compare);
                btmNav.setOnClickListener(this);
            } else {
                btmNav.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_history;
    }

    @Override
    protected void initView(View rootView) {
        excel = rootView.findViewById(R.id.customExcel);
        String[] headers = getResources().getStringArray(R.array.excel_title_simple_report_list);

        List<Method> methodList = ModelUtils.getGetters(SimpleReport.class, new String[]{"getDate", "getTime", "getMemberName", "getGender", "getAge"});
        excel.setup(headers, new int[]{2, 6, 4, 4, 3, 3, 2}, new ArrayList<>(), methodList);
        excel.setOnRowStateChangedListener(this);

        SearchView searchView = rootView.findViewById(R.id.search);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        ((TextView) searchView.findViewById(id)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_small));

        Button btnSearch = rootView.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(view -> {
        });
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        chosenReportTimeStamp = bundle.getLong(Constants.BUNDLE_KEY_TIME_STAMP, 0L);
        if (chosenReportTimeStamp > 0) {
            initToolNavBar();
        }
    }

    @Override
    public void onBindViewHolder(LinearLayout row, int position) {
        row.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.BUNDLE_KEY_TIME_STAMP, excel.getData(position).getTimeMills());
            NavHostFragment.findNavController(this).navigate(R.id.history_to_report, bundle);
        });
    }

    @Override
    protected void initObserver() {
        viewModel.getAllSimpleReports().observe(this, data -> {
            excel.reload(data);
            SimpleReport chosenReport = findReport(data, chosenReportTimeStamp);
            //TODO: 考虑查找失败的情况
            if (chosenReport != null) {
                excel.choose(chosenReport);
            }
        });
    }

    private static SimpleReport findReport(List<SimpleReport> data, long chosenReportTimeStamp) {
        if (chosenReportTimeStamp > 0) {
            for (SimpleReport report : data) {
                if (report.getTimeMills() == chosenReportTimeStamp) {
                    return report;
                }
            }
        }
        return null;
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onClick(View v) {

    }
}
