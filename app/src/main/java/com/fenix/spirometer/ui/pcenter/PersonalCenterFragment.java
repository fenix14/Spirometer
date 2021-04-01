package com.fenix.spirometer.ui.pcenter;

import android.os.Bundle;

import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;

public class PersonalCenterFragment extends BaseVMPrefFragment {
    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(false);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(null);
        toolbar.setLeftText(null);
        toolbar.setRightText(null);

        viewModel.setShowNavBar(true);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        Bundle bundle = new Bundle();
		int action;
        if ("administrators".equals(key)) {
            action = R.id.personal_to_operators;
        } else if ("estimated_value_config".equals(key)) {
            action = R.id.personal_to_est_values;
        } else if ("detector_calibration".equals(key)) {
            action = R.id.personal_to_detector_calibration;
        } else if(preference.getKey().equals("contact_us")){
            bundle.putString("flag_deaults","contact");
			action = R.id.personal_to_others;
        } else if(preference.getKey().equals("version")){
            bundle.putString("flag_deaults","version");
			action = R.id.personal_to_others;
        } else if(preference.getKey().equals("mac")){
            bundle.putString("flag_deaults","mac");
			action = R.id.personal_to_others;
        } else if(preference.getKey().equals("account")){
            bundle.putString("flag_deaults","account");
			action = R.id.personal_to_others;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
		NavHostFragment.findNavController(this).navigate(action, bundle);
        return true;
    }

    @Override
    protected void initPreference() {
    }

    @Override
    protected void initObserver() {
        viewModel.getLoginState().observe(this, loginState -> {
            if (loginState != null && loginState.isLogin()) {
                setHeader(loginState.getLoginOperator().getDisplayName(), loginState.getLoginOperator().getDuty());
                PreferenceCategory adminCategory = findPreference("admin_category");
                if (adminCategory != null) {
                    adminCategory.setVisible(loginState.getLoginOperator().isAdmin());
                }
            }
        });
    }

    @Override
    protected int getPrefId() {
        return R.xml.pref_personal_center;
    }

}
