package com.fenix.spirometer.ui.login;

import androidx.annotation.Nullable;

import java.util.Optional;

/**
 * 登陆页面输入状态
 */
public class InputDataState {
    private boolean isUserNameValid;
    private boolean isPasswordValid;

    public InputDataState(boolean isUserNameValid, boolean isPasswordValid) {
        this.isUserNameValid = isUserNameValid;
        this.isPasswordValid = isPasswordValid;
    }

    public boolean isUserNameValid() {
        return isUserNameValid;
    }

    public boolean isPasswordValid() {
        return isPasswordValid;
    }

    public boolean isInputValid() {
        return isUserNameValid && isPasswordValid;
    }
}