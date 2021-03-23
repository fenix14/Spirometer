package com.fenix.spirometer.ui.history;

import android.view.View;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.room.TestReportRepository;
import com.fenix.spirometer.room.model.History;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;

import java.util.List;

public class HistoryFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener {

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
       TestReportRepository.getInstance().getSimpleReports().observe(this, simpleHistories -> {
       });

    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }
}
