package com.fenix.spirometer.ui.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;

import com.fenix.spirometer.R;
import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.model.BaseModel;
import com.fenix.spirometer.ui.main.MainViewModel;
import com.fenix.spirometer.ui.widget.CustomPreference;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class BaseVMPrefFragment extends PreferenceFragmentCompat {
    protected MainViewModel viewModel;
    private CustomToolbar toolbar;
    private Button btnFooter;
    private View vHeader;
    private String headerTitle;
    private String headerSummary;
    private TextView tvHeaderTitle, tvHeaderSummary;

    protected CustomToolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(getPrefId());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(MyApplication.getInstance())).
                get(MainViewModel.class);
        toolbar = (CustomToolbar) ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        btnFooter = view.findViewById(R.id.footer);
        vHeader = view.findViewById(R.id.header);

        tvHeaderTitle = view.findViewById(R.id.header_title);
        tvHeaderSummary = view.findViewById(R.id.header_summary);

        if (headerTitle == null && headerSummary == null) {
            vHeader.setVisibility(View.GONE);
        } else {
            vHeader.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(headerTitle)) {
                tvHeaderTitle.setText(headerTitle);
            }
            if (!TextUtils.isEmpty(headerSummary)) {
                tvHeaderSummary.setText(headerSummary);
            }
        }
        initToolNavBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPreference();
        initObserver();
        initData();
    }

    protected void initData() {
    }

    protected abstract void initPreference();

    protected abstract void initToolNavBar();

    @XmlRes
    protected abstract int getPrefId();

    protected void initObserver() {
    }

    protected Button getFooter() {
        return btnFooter;
    }

    protected void setHeader(String title, String summary) {
        headerTitle = title;
        headerSummary = summary;
        if (vHeader == null) {
            return;
        }
        if (headerTitle == null && headerSummary == null) {
            vHeader.setVisibility(View.GONE);
        } else {
            vHeader.setVisibility(View.VISIBLE);
            if (tvHeaderTitle != null && !TextUtils.isEmpty(headerTitle)) {
                tvHeaderTitle.setText(headerTitle);
            }
            if (tvHeaderSummary != null && !TextUtils.isEmpty(headerSummary)) {
                tvHeaderSummary.setText(headerSummary);
            }
        }
    }

    @Nullable
    protected  <T extends BaseModel> T createModelFromPref(Class<T> clazz) {
        Preference preference = null;
        T model;
        try {
            model = clazz.newInstance();
            Method setter;

            PreferenceScreen ps = getPreferenceScreen();
            int preferenceCount = ps.getPreferenceCount();
            CustomPreference customPreference;
            for (int i = 0; i < preferenceCount; i++) {
                preference = ps.getPreference(i);
                if (!(preference instanceof CustomPreference)) {
                    continue;
                }
                customPreference = (CustomPreference) preference;
                String content = customPreference.getInput();
                if (customPreference.isNecessary() && TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), customPreference.getTitle() + "不能为空", Toast.LENGTH_SHORT).show();
                    return null;
                }
                setter = ModelUtils.getSetter(clazz, customPreference.getKey(), String.class);
                if (setter != null) {
                    setter.invoke(model, content);
                }
            }
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Found error while trying new instance of " + clazz.getName(), Toast.LENGTH_SHORT).show();
            return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            String prefKey = preference == null ? "null yet" : preference.getKey();
            Toast.makeText(getActivity(), "Found error while trying " + prefKey, Toast.LENGTH_SHORT).show();
            return null;
        }
        return model;
    }
}
