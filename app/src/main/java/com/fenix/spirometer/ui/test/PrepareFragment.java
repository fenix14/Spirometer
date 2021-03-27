package com.fenix.spirometer.ui.test;

import android.view.View;
import android.widget.Button;

import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;

public class PrepareFragment extends BaseVMFragment implements CustomToolbar.OnItemClickListener {
    @Override
    protected void initToolNavBar() {
        viewModel.setShowNavBar(false);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(getString(R.string.tab_prepare));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

//        Button btmNav = getFooter();
//        btmNav.setVisibility(View.VISIBLE);
//        btmNav.setText(R.string.btn_save);
//        btmNav.setOnClickListener(this);
//        btmNav.setText(viewModel.isTesting() ? R.string.btn_next : R.string.btn_save);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_prepare;
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
}
