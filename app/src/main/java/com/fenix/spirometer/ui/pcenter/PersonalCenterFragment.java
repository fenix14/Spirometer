package com.fenix.spirometer.ui.pcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Administrator;
import com.fenix.spirometer.ui.base.BaseToolbarVmFragment;
import com.fenix.spirometer.util.InfomationRepository;

public class PersonalCenterFragment extends BaseToolbarVmFragment {
    private TextView title, summary;
    private RecyclerView preference;
    private TextView tvVersion, tvMac;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_personal;
    }

    @Override
    protected void initView(View rootView) {
        title = rootView.findViewById(R.id.title);
        summary = rootView.findViewById(R.id.summary);
        summary = rootView.findViewById(R.id.summary);
        summary = rootView.findViewById(R.id.summary);
        tvVersion = rootView.findViewById(R.id.content_version);
        tvMac = rootView.findViewById(R.id.content_mac);
        initData();
    }

    private void initData() {
        tvVersion.setText(InfomationRepository.getAppVersionName());
        tvMac.setText(InfomationRepository.getMacAddress());
    }

    @Override
    protected void initObserver() {
        viewModel.subscribeToAdministrator(this, administrator -> {
            if (administrator == null) {
                return;
            }
            title.setText(administrator.getDisplayName());
            summary.setText(administrator.getDuty());
        });
    }
}