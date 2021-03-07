package com.fenix.spirometer.ui.login;

import android.app.Activity;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Administrator;
import com.fenix.spirometer.ui.main.MainActivity;
import com.fenix.spirometer.util.AllViewModelFactory;

/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText etUserName, etPassword;
    private Button btnLogin;
    private ProgressBar pbLoading;

    private TextWatcher afterTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @Override
        public void afterTextChanged(Editable s) {
            loginViewModel.inputDataChanged(etUserName.getText().toString(),
                    etPassword.getText().toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new AllViewModelFactory())
                .get(LoginViewModel.class);

        initView();
        initObserver();
    }

    private void initView() {
        etUserName = findViewById(R.id.username);
        etUserName.addTextChangedListener(afterTextChangedListener);

        etPassword = findViewById(R.id.password);
        etPassword.addTextChangedListener(afterTextChangedListener);

        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> {
            pbLoading.setVisibility(View.VISIBLE);
            loginViewModel.login(etUserName.getText().toString(), etPassword.getText().toString());
        });

        pbLoading = findViewById(R.id.loading);
    }

    private void initObserver() {
        loginViewModel.getInputDataState().observe(this, inputDataState -> {
            btnLogin.setEnabled(inputDataState.isInputValid());
            if (!inputDataState.isUserNameValid()) {
                etUserName.setError(getString(R.string.invalid_username));
            }
            if (!inputDataState.isPasswordValid() && etPassword.isFocused()) {
                etPassword.setError(getString(R.string.invalid_password));
            }
        });

        loginViewModel.getAdministrator().observe(this, admin -> {
            pbLoading.setVisibility(View.GONE);
            if (admin == null) {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class).putExtra("admin", admin);
            startActivity(intent);
            finish();
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}