package com.fenix.spirometer.model;

import androidx.annotation.IntDef;

public class LoginState {
    private Operator loginOperator;

    private boolean isLogin;

    @ErrType
    private int err;

    private String errMessage;

    public LoginState() {
    }

    public LoginState(Operator loginOperator, boolean isLogin, @ErrType int err, String errMessage) {
        this.loginOperator = loginOperator;
        this.isLogin = isLogin;
        this.err = err;
        this.errMessage = errMessage;
    }

    public Operator getLoginOperator() {
        return loginOperator;
    }

    public void setLoginOperator(Operator loginOperator) {
        this.loginOperator = loginOperator;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    @IntDef({ErrType.ERR_NONE, ErrType.ERR_PASSWORD, ErrType.ERR_USER_ID, ErrType.ERR_UNKNOWN, ErrType.ERR_PWD_CHANGED_SUCCESS, ErrType.ERR_PWD_CHANGED_FAIL})
    public @interface ErrType {
        int ERR_NONE = 0;
        int ERR_PASSWORD = 1;
        int ERR_USER_ID = 2;
        int ERR_UNKNOWN = 3;
        int ERR_PWD_CHANGED_SUCCESS = 4;
        int ERR_PWD_CHANGED_FAIL = 5;
    }
}
