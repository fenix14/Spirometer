package com.fenix.spirometer.ui.pcenter.estvaluelist;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.widget.CustomPreference;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.ModelUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EditEstValueFragment extends BaseVMPrefFragment implements CustomToolbar.OnItemClickListener {
    private EditEstValueViewModel evViewModel;
    private View.OnClickListener clickListener = view -> {
        if (view.getId() != R.id.footer) {
            return;
        }

        PreferenceScreen ps = getPreferenceScreen();
        int prefCount = ps.getPreferenceCount();
        EstValue estValue = new EstValue();
        Preference preference = null;
        CustomPreference customPreference = null;
        Method setter;
        try {
            for (int i = 0; i < prefCount; i++) {
                preference = ps.getPreference(i);
                if (!(preference instanceof CustomPreference)) {
                    continue;
                }
                customPreference = (CustomPreference) preference;
                String content = customPreference.getInput();
                if (customPreference.isNecessary() && TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), customPreference.getTitle() + "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                setter = ModelUtils.getSetter(EstValue.class, customPreference.getKey(), String.class);
                if (setter != null) {
                    setter.invoke(estValue, content);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Found error while trying " + preference.getKey(), Toast.LENGTH_SHORT).show();
            return;
        }
        evViewModel.updateEstValue(estValue);
        NavHostFragment.findNavController(this).navigateUp();
    };

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(R.string.tab_member_detail));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        btmNav.setVisibility(View.VISIBLE);
        btmNav.setText(R.string.btn_save);
        btmNav.setOnClickListener(clickListener);
    }

    @Override
    protected void initPreference() {
        evViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(EditEstValueViewModel.class);

        Bundle bundle = getArguments();
        Serializable data;
        if (bundle == null) {
            Log.d("hff", "None value");
            Toast.makeText(requireContext(), "数据读取失败，请返回重试", Toast.LENGTH_SHORT).show();
            return;
        }
        data = bundle.getSerializable(EstimatedValuesFragment.KEY_EST_VALUE);
        if (!(data instanceof EstValue)) {
            Log.d("hff", "Invalid value");
            Toast.makeText(requireContext(), "数据错误，请返回重试", Toast.LENGTH_SHORT).show();
            return;
        }
        EstValue estValue = (EstValue) data;
        CustomPreference pref;
        pref = findPreference("setName");
        pref.setContent(estValue.getName());

        pref = findPreference("setGender");
        pref.setContent(estValue.getGender());

        pref = findPreference("setFormula");
        pref.setContent(estValue.getFormula());

        pref = findPreference("setValueR");
        pref.setContent(String.valueOf(estValue.getValueR()));

    }

    @Override
    protected int getPrefId() {
        return R.xml.pref_est_value_edit;
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }
}
