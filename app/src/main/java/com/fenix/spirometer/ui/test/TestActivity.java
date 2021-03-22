package com.fenix.spirometer.ui.test;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.main.MainViewModel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;

public class TestActivity extends AppCompatActivity {
    CustomToolbar customToolbar;
    private TestViewModel testViewModel;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(TestViewModel.class);
        initView();
        initObserver();
        initData();
    }

    private void initView() {
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.toolbar_main);
            actionBar.setDisplayShowTitleEnabled(false);
            customToolbar = (CustomToolbar) actionBar.getCustomView();
        }

        navController = Navigation.findNavController(this, R.id.frag_nav_host);
    }

    private void initObserver() {
    }

    private void initData() {

    }
}