package com.fenix.spirometer.room;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.model.LoginState;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.room.database.AppDatabase;
import com.fenix.spirometer.util.Utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管理员信息仓库入口
 */
public class OperatorRepository {
    private static volatile OperatorRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor;
    private final MutableLiveData<LoginState> mdLoginState = new MutableLiveData<>();
    final MutableLiveData<List<Operator>> mdOperators = new MutableLiveData<>();
    private Context context;

    public OperatorRepository() {
        context = MyApplication.getInstance();
        database = AppDatabase.getInstance();
        executor = Executors.newCachedThreadPool();
    }

    public static OperatorRepository getInstance() {
        if (instance == null) {
            instance = new OperatorRepository();
        }
        return instance;
    }

    public void login(@NonNull String userId, @NonNull String password) {
        executor.execute(() -> {
            LoginState loginState;
            if (!Utils.isUserIdValid(userId)) {
                loginState = new LoginState(null, false, LoginState.ErrType.ERR_USER_ID, "账号格式有误");
            } else if (!Utils.isPasswordValid(password)) {
                loginState = new LoginState(null, false, LoginState.ErrType.ERR_USER_ID, "密码有误");
            } else {
                Operator operator = database.operatorDao().getOperator(userId);
                if (operator == null) {
                    loginState = new LoginState(null, false, LoginState.ErrType.ERR_USER_ID, "账号有误");
                } else if (!operator.getPassword().equals(password)) {
                    loginState = new LoginState(null, false, LoginState.ErrType.ERR_PASSWORD, "密码有误");
                } else {
                    loginState = new LoginState(operator, true, LoginState.ErrType.ERR_NONE, "");
                }
            }
            mdLoginState.postValue(loginState);
        });
    }

    // 登录相关
    public void logout() {
        mdLoginState.postValue(null);
    }

    public void changePassword(@NonNull String oldPassword, @NonNull String newPassword) {
        executor.execute(() -> {
            LoginState loginState = mdLoginState == null || mdLoginState.getValue() == null ? null : mdLoginState.getValue();
            if (loginState == null || !oldPassword.equals(loginState.getLoginOperator().getPassword())) {
                Log.d("hff", "old password = " + oldPassword + ", current password = " + loginState.getLoginOperator().getPassword());
                loginState.setErr(LoginState.ErrType.ERR_PWD_CHANGED_FAIL);
                loginState.setErrMessage("旧密码错误");
            } else {
                Operator loginOperator = loginState.getLoginOperator();
                loginOperator.setPassword(newPassword);
                int id = database.operatorDao().updateOperator(loginOperator);
                if (id < 0) {
                    loginState.setErr(LoginState.ErrType.ERR_PWD_CHANGED_FAIL);
                    loginState.setErrMessage("数据库异常，修改失败");
                } else {
                    loginState.setLoginOperator(loginOperator);
                    loginState.setErr(LoginState.ErrType.ERR_PWD_CHANGED_SUCCESS);
                    loginState.setErrMessage(null);
                }
            }
            mdLoginState.postValue(loginState);
        });
    }

    public MutableLiveData<LoginState> getLoginState() {
        return mdLoginState;
    }

    // 操作人员你相关
    public void insertOperator(List<Operator> operators) {
        executor.execute(() -> {
            database.operatorDao().insertOperators(operators);
            loadAllOperators();
        });
    }

    public void insertOperator(Operator operator) {
        executor.execute(() -> {
            database.operatorDao().insertOperator(operator);
            loadAllOperators();
        });
    }

    public MutableLiveData<List<Operator>> loadAllOperators() {
        executor.execute(() -> mdOperators.postValue(database.operatorDao().getOperators()));
        return mdOperators;
    }

    public void deleteOperator(Operator operator) {
        if (operator == null) {
            return;
        }
        executor.execute(() -> {
            database.operatorDao().deleteOperator(operator);
            loadAllOperators();
            if (mdLoginState.getValue() != null && operator.equals(mdLoginState.getValue().getLoginOperator())) {
                Log.d("hff", "logout int deleteOperator");
                logout();
            }
        });
    }

    public MutableLiveData<Operator> getOperator(String userId) {
        final MutableLiveData<Operator> mdOperator = new MutableLiveData<>();
        executor.execute(() -> mdOperator.postValue(database.operatorDao().getOperator(userId)));
        return mdOperator;
    }
}
