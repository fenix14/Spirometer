package com.fenix.spirometer.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.widget.CustomToolbar;

public abstract class BaseToolbarFragment extends Fragment {
    public static final String FLAG_TOOLBAR_TITLE = "toolbar_title";
    public static final String FLAG_TOOLBAR_SHOW_BACK = "toolbar_is_show_back";
    public static final String FLAG_TOOLBAR_ITEM = "toolbar_item";
    public static final String FLAG_TOOLBAR_BG_COLOR = "toolbar_bg_color";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        initToolbar(rootView, getArguments());
        setHasOptionsMenu(true);
        initView(rootView);
        return rootView;
    }

    protected void initToolbar(View rootView, Bundle arguments) {
        setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        CustomToolbar customToolbar = (CustomToolbar) actionBar.getCustomView();
        customToolbar.clear();

        Bundle data = getArguments();
        String item = data == null ? "" : data.getString(FLAG_TOOLBAR_ITEM, "");
        Log.d("hff", "onCreateOptionsMenu:item = " + item);
        customToolbar.setRight(item);

        String title = data == null ? "" : data.getString(FLAG_TOOLBAR_TITLE, "");
        Log.d("hff", "onCreateOptionsMenu:title = " + title);
        customToolbar.setCenter(title);
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View rootView);


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        //menu.clear();
        //inflater.inflate(R.menu.toolbar, menu);
        //menu.getItem(0).setTitle(item);
//
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        CustomToolbar customToolbar = (CustomToolbar) actionBar.getCustomView();
//        customToolbar.clear();
//
//        Bundle data = getArguments();
//        String item = data == null ? "" : data.getString(FLAG_TOOLBAR_ITEM, "");
//        Log.d("hff", "onCreateOptionsMenu:item = " + item);
//        customToolbar.setRight(item);
//
//        String title = data == null ? "" : data.getString(FLAG_TOOLBAR_TITLE, "");
//        Log.d("hff", "onCreateOptionsMenu:title = " + title);
//        customToolbar.setCenter(title);
    }
}
