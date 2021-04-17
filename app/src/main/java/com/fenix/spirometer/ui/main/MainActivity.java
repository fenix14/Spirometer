package com.fenix.spirometer.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.login.LoginActivity;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * 首页承载Activity
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView btmNavigation;
    NavController navController;
    MainViewModel viewModel;
    CustomToolbar customToolbar;
    Toolbar toolbar;
    ActionBar actionBar;
    private AlertDialog exitConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(MainViewModel.class);
        if (!viewModel.isLogin()) {
            Log.d("hff", "onCreate logout int mainActivity");
            logout();
            return;
        }
        initView();
        initObserver();
        initData();
    }

    private void initView() {
        // 设置标题栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.toolbar_main);
            actionBar.setDisplayShowTitleEnabled(false);
            customToolbar = (CustomToolbar) actionBar.getCustomView();
        }

        // 设置底部导航栏
        navController = Navigation.findNavController(this, R.id.frag_nav_host);
        btmNavigation = findViewById(R.id.btm_nav);
        NavigationUI.setupWithNavController(btmNavigation, navController);
        btmNavigation.setOnNavigationItemSelectedListener(this);
    }


    private void initData() {
    }

    private void initObserver() {
        viewModel.subscribeToIsShowNavBar(this,
                isShowNavBar -> btmNavigation.setVisibility(isShowNavBar != null && isShowNavBar ? View.VISIBLE : View.GONE));
        //viewModel.subscribeToIsLightToolbar(this, this::setupToolbar);

        viewModel.getLoginState().observe(this, loginState -> {
            if (loginState == null || !loginState.isLogin()) {
                Log.d("hff", "logout int mainActivity");
                logout();
            }
        });

    }

    private void setupToolbar(boolean isLightType) {
        int backgroundColor = isLightType ? R.drawable.bg_white : R.drawable.bg_blue;
        int textColor = isLightType ? R.color.black : R.color.white;
        actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, backgroundColor));
        customToolbar.setBackgroundResource(backgroundColor);
        customToolbar.setTextColor(getColor(textColor));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.isChecked()) {
            Toast.makeText(this, "Already checked.", Toast.LENGTH_SHORT).show();
            return true;
        }
        navController.navigate(item.getItemId());
        return false;
    }

    private void logout() {
        Log.d("hff", "logout");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void releaseAll() {
    }

    @Override
    public void onBackPressed() {
        CharSequence label = navController.getCurrentDestination().getLabel();
        if (label != null) {
            if ("main".contentEquals(label) && !viewModel.isTesting()) {
                showExitConfirmDialog();
            } else if ("testing".contentEquals(label)) {
                // 测试页面忽略返回键
            } else if ("none_pre".contentEquals(label)) {
                navController.navigate(R.id.frag_home);
            }
        } else {
            navController.navigateUp();
        }
    }

    private void showExitConfirmDialog() {
        if (exitConfirmDialog == null) {
            exitConfirmDialog = Utils.createConfirmDialog(this, getString(R.string.confirm_exit),
                    (dialogInterface, which) -> {
                        if (which == BUTTON_POSITIVE) {
                            releaseAll();
                            finish();
                        }
                    });
        }
        exitConfirmDialog.show();
    }
}