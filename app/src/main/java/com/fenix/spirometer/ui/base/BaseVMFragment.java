package com.fenix.spirometer.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fenix.spirometer.ui.main.MainViewModel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;

/**
 * 定制Toolbar和NavigationBar的基本Fragment
 */
public abstract class BaseVMFragment extends Fragment {
    public static final String FLAG_TOOLBAR_TITLE = "toolbar_title";
    public static final String FLAG_TOOLBAR_LEFT = "toolbar_left";
    public static final String FLAG_TOOLBAR_RIGHT = "toolbar_item";
    public static final String FLAG_TOOLBAR_BG_TYPE = "toolbar_bg_color";
    public static final String FLAG_NAV_BG_TYPE = "nav_bg_color";
    protected MainViewModel viewModel;
    private CustomToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity(), new AllViewModelFactory()).get(MainViewModel.class);
        View rootView = inflater.inflate(getLayoutId(), container, false);
        setHasOptionsMenu(true);
        toolbar = (CustomToolbar) ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
        initToolNavBar();
        initView(rootView);
        initObserver();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    protected void initData() {
    }

    protected abstract void initToolNavBar();

    protected CustomToolbar getToolbar() {
        return toolbar != null ? toolbar : (CustomToolbar) ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initView(View rootView);

    protected void initObserver() {
    }
}
