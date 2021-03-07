package com.fenix.spirometer.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Administrator;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 首页承载Activity
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView btmNavigation;
    NavHostFragment navHostFragment;
    NavController navController;
    MainViewModel viewModel;
    Toolbar toolbar;
    CustomToolbar customToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
        Intent intent = getIntent();
        Administrator admin;
//        if (intent == null || (admin = (Administrator) intent.getSerializableExtra("admin")) == null) {
//            //TODO
//            finish();
//            return;
//        }
//        viewModel.setAdministrator(admin);
        setContentView(R.layout.activity_main);
        initView();
        initObserver();
    }

    private void initObserver() {
//        viewModel.subscribeToNavigationBarBg(this, type -> {
//            if (type == null || type == -1) {
//                btmNavigation.setVisibility(View.INVISIBLE);
//            } else {
//                btmNavigation.setBackgroundResource(type);
//                btmNavigation.setVisibility(View.VISIBLE);
//            }
//        });
//        viewModel.subscribeToToolbarType(this, type -> {
//            switch (type) {
//                Constants.BgType.TYPE_GIL:
//                break;
//                Constants.BgType.TYPE_DARK:
//                default:
//                    break;
//            }
//                toolbar.setBackgroundResource(();
//        });
    }

//    private void initToolbarAndNavigationBar(@Constants.BgType int toolbarType, @Constants.BgType int navType) {
//        customToolbar.clear();
//
//        int navType = data.getInt(FLAG_NAV_BG_TYPE, Constants.BgType.TYPE_LIGHT);
//        int toolbarType = data.getInt(FLAG_TOOLBAR_BG_TYPE, Constants.BgType.TYPE_DARK);
//        viewModel.setNavigationBarBg(navType);
//        viewModel.setToolbarType(toolbarType);
//        customToolbar.setCenterText(data.getString(FLAG_TOOLBAR_TITLE, null));
//        customToolbar.setLeftText(data.getString(FLAG_TOOLBAR_LEFT, null));
//        customToolbar.getLeftButton().setOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());
//        customToolbar.setRightText(data.getString(FLAG_TOOLBAR_RIGHT, null));
//        customToolbar.getRightButton().setOnClickListener(this::onClickToolbarRight);
//
//    }

    private void initView() {
        // 设置标题栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.toolbar_main);
        actionBar.setDisplayShowTitleEnabled(false);
        customToolbar = (CustomToolbar) actionBar.getCustomView();

        // 设置底部导航栏
        navController = Navigation.findNavController(this, R.id.frag_nav_host);
        btmNavigation = findViewById(R.id.btm_nav);
        NavigationUI.setupWithNavController(btmNavigation, navController);
        btmNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            Toast.makeText(this, "Already checked.", Toast.LENGTH_SHORT).show();
            return true;
        }
        // make sure the menu item ids equals to the navigation item ids.
        navController.navigate(item.getItemId());
        return false;
    }
}