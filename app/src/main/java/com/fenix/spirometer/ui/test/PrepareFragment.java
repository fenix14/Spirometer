package com.fenix.spirometer.ui.test;

import android.view.View;
import android.widget.Button;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;

public class PrepareFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener, View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.frag_prepare;
    }

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.clear();
            toolbar.setCenterText(getString(R.string.tab_prepare));
            toolbar.setLeftText(getString(R.string.item_back));
            toolbar.setRightText(null);
            toolbar.setOnItemClickListener(this);
        }

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        btmNav.setVisibility(View.VISIBLE);
        btmNav.setText(R.string.footer_start_testing);
        btmNav.setOnClickListener(this);
    }

    @Override
    protected void initView(View rootView) {
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
        if (v.getId() == R.id.footer) {
            NavHostFragment.findNavController(this).navigate(R.id.prepare_to_testing);
        }
    }
}
