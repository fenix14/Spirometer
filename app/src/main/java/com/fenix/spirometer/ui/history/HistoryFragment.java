package com.fenix.spirometer.ui.history;

import android.view.View;
import android.widget.LinearLayout;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.SimpleReport;
import com.fenix.spirometer.room.TestReportRepository;
import com.fenix.spirometer.room.model.History;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener, CustomExcel.OnRowStateChangeListener {
    private CustomExcel<SimpleReport> excel;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowNavBar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(getString(R.string.tab_member_list));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);
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
        excel.setup(headers, new int[]{1, 6, 4, 4, 3, 3, 2}, new ArrayList<>(), methodList);
        excel.setOnRowStateChangedListener(this);
    }

    @Override
    public void onBindViewHolder(LinearLayout row, int position) {

    }

    @Override
    protected void initObserver() {
        viewModel.getAllSimpleReports().observe(this, excel::reload);
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }
}
