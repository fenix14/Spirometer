package com.fenix.spirometer.ui.login;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.model.Province;
import com.fenix.spirometer.ui.main.MainActivity;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.FileParser;

import java.util.List;

import static com.fenix.spirometer.util.Constants.SP_KEY_IS_INITIALIZED;

/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;

    private EditText etUserId, etPassword;
    private Button btnLogin;
    private ProgressBar pbLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new AllViewModelFactory())
                .get(LoginViewModel.class);

        initView();
        initObserver();
        initData();
    }


    private void initView() {
        etUserId = findViewById(R.id.userId);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> {
            pbLoading.setVisibility(View.VISIBLE);
            loginViewModel.login(etUserId.getText().toString(), etPassword.getText().toString());
        });

        pbLoading = findViewById(R.id.loading);
    }

    private void initObserver() {
        loginViewModel.subscribeToLoginState(this, loginState -> {
            pbLoading.setVisibility(View.GONE);
            if (loginState == null) {
                return;
            }
            if (loginState.isLogin()) {
                startApp();
            } else {
                Toast.makeText(this, loginState.getErrMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences(Constants.SP_NAME, 0);
        if (!sp.getBoolean(SP_KEY_IS_INITIALIZED, false)) {
            // TODO: 调试初始化数据
            List<Province> provinces = FileParser.parserProvince(getResources().openRawResource(R.raw.provinces));
            loginViewModel.insertProvinces(provinces);

            List<DetectorCompensation> compensations = FileParser.parserDetectorCompensations(getResources().openRawResource(R.raw.detector_compensation));
            loginViewModel.insertDetectorCompensations(compensations);

            loginViewModel.insertEstValues();

            loginViewModel.insertOperators();

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(SP_KEY_IS_INITIALIZED, true);
            editor.apply();
        }

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_PRIVILEGED
                , Manifest.permission.ACCESS_WIFI_STATE};
        requestPermissions(permissions, 0);
    }

    private void startApp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}