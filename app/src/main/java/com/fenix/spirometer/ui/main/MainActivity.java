package com.fenix.spirometer.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView btmNavigation;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        // 设置标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.toolbar_main);
        actionBar.setDisplayShowTitleEnabled(false);

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