package com.fenix.spirometer.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fenix.spirometer.ui.main.MainViewModel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;

/**
 * 定制Toolbar和NavigationBar的基本Fragment
 */
public abstract class BaseToolbarVmFragment extends Fragment {
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
        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).
                get(MainViewModel.class);
        View rootView = inflater.inflate(getLayoutId(), container, false);
        initToolbarAndNavigationBar(rootView, getArguments());
        setHasOptionsMenu(true);
        initView(rootView);
        initObserver();
        return rootView;
    }

    protected void initToolbarAndNavigationBar(View rootView, Bundle arguments) {
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        CustomToolbar customToolbar = (CustomToolbar) actionBar.getCustomView();
        customToolbar.clear();
        Bundle data = getArguments();
        if (data == null) {
            return;
        }

        int navType = data.getInt(FLAG_NAV_BG_TYPE, Constants.BgType.TYPE_LIGHT);
        int toolbarType = data.getInt(FLAG_TOOLBAR_BG_TYPE, Constants.BgType.TYPE_DARK);
        viewModel.setNavigationBarBg(navType);
        viewModel.setToolbarType(toolbarType);
        customToolbar.setCenterText(data.getString(FLAG_TOOLBAR_TITLE, null));
        customToolbar.setLeftText(data.getString(FLAG_TOOLBAR_LEFT, null));
        customToolbar.getLeftButton().setOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());
        customToolbar.setRightText(data.getString(FLAG_TOOLBAR_RIGHT, null));
        customToolbar.getRightButton().setOnClickListener(this::onClickToolbarRight);

    }

    protected CustomToolbar getToolbar() {
        return toolbar != null ? toolbar : (CustomToolbar) ((AppCompatActivity) getActivity()).getSupportActionBar().getCustomView();
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View rootView);

    protected void initObserver() {
    }

    protected void onClickToolbarLeft(View view) {
    }

    protected void onClickToolbarRight(View view) {
    }
}
