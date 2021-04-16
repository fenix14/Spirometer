package com.fenix.spirometer.ui.pcenter.others;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.LoginState;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;

public class CurrencyInfoFragment extends BaseVMFragment implements View.OnClickListener, CustomToolbar.OnItemClickListener {
    private String mParam1;
    CustomToolbar toolbar;
    LinearLayout mlayouthospital;
    LinearLayout mlayoutDepartment;
    LinearLayout mlayoutphone;
    LinearLayout mlayoutversion;
    LinearLayout mlayoutaccount;
    EditText mhospital;
    EditText mdepartment;
    EditText mphonenumber;
    Button mexitButton;
    boolean isaccount = false;
    boolean iscontact = false;
    TextView change_password;
    private AlertDialog changePwdDialog;
    private final String HOSPITAL_KEY = "hospital_key";
    private final String DEPARTMENT_KEY = "department_key";
    private final String PHONENUMBER_KEY = "phonenumber_key";

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText("联系我们");
        toolbar.setLeftText("<");
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        btmNav.setVisibility(View.GONE);
    }

    @LayoutRes
    protected int getLayoutId() {
        return R.layout.fragment_personaldeaults;
    }

    protected void initView(View rootView) {
        toolbar = getToolbar();
        toolbar.clear();
        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setLeftText("<");
        mlayouthospital = rootView.findViewById(R.id.layout_hospital);
        mlayoutDepartment = rootView.findViewById(R.id.layout_Department);
        mlayoutphone = rootView.findViewById(R.id.layout_phone);
        mlayoutversion = rootView.findViewById(R.id.layout_version);
        mlayoutaccount = rootView.findViewById(R.id.layout_account);
        mlayoutaccount.setOnClickListener(this);
        mexitButton = rootView.findViewById(R.id.button);
        change_password = (rootView).findViewById(R.id.change_password);
        mhospital = (rootView).findViewById(R.id.hospital_value);
        mdepartment = (rootView).findViewById(R.id.department_value);
        mphonenumber = (rootView).findViewById(R.id.phone_value);
        mexitButton.setOnClickListener(this);

        if (getArguments() != null) {
            mParam1 = getArguments().getString("flag_defaults", "");
        }
        switch (mParam1) {
            case "contact":
                iscontact = true;
                toolbar.setCenterText("联系我们");
                mlayoutversion.setVisibility(View.GONE);
                mlayoutaccount.setVisibility(View.GONE);
                mexitButton.setText("保存");
                break;
            case "version":
                toolbar.setCenterText("版本信息");
                mlayouthospital.setVisibility(View.GONE);
                mlayoutDepartment.setVisibility(View.GONE);
                mlayoutphone.setVisibility(View.GONE);
                mlayoutaccount.setVisibility(View.GONE);
                mexitButton.setVisibility(View.GONE);
                break;
            case "account":
                toolbar.setCenterText("账号管理");
                isaccount = true;
                mlayouthospital.setVisibility(View.GONE);
                mlayoutDepartment.setVisibility(View.GONE);
                mlayoutphone.setVisibility(View.GONE);
                mlayoutversion.setVisibility(View.GONE);
                change_password.setText(">");
                break;
            default:
                NavHostFragment.findNavController(this).navigateUp();
                break;
        }
    }

    @Override
    protected void initData() {
        if (getContext() == null) {
            return;
        }
        if (getArguments() != null && "contact".equals(getArguments().getString("flag_defaults", ""))) {
            SharedPreferences sp = getContext().getSharedPreferences(Constants.SP_NAME, 0);
            mhospital.setText(sp.getString(HOSPITAL_KEY, ""));
            mdepartment.setText(sp.getString(DEPARTMENT_KEY, ""));
            mphonenumber.setText(sp.getString(PHONENUMBER_KEY, ""));
        }
    }

    @Override
    protected void initObserver() {
        viewModel.subscribeToLoginState(this, loginState -> {
            if (loginState == null) {
                return;
            }
            if (loginState.getErr() == LoginState.ErrType.ERR_PWD_CHANGED_SUCCESS) {
                new AlertDialog.Builder(getActivity()).setMessage("密码修改成功，请重新登录。")
                        .setPositiveButton(R.string.dialog_ok, (dialog, which) -> viewModel.logout())
                        .setCancelable(false).create().show();
            } else if (loginState.getErr() == LoginState.ErrType.ERR_PWD_CHANGED_FAIL) {
                Toast.makeText(getContext(), loginState.getErrMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                Log.d("wuxin", "exit========");
                if (getContext() == null) {
                    return;
                }
                if (iscontact) {
                    Editable hospital = mhospital.getText();
                    Editable department = mdepartment.getText();
                    Editable phonenumber = mphonenumber.getText();
                    if (TextUtils.isEmpty(hospital) || TextUtils.isEmpty(department) || TextUtils.isEmpty(phonenumber)) {
                        Toast.makeText(getContext(), R.string.pref_no_input_empyt, Toast.LENGTH_LONG).show();
                        return;
                    }
                    SharedPreferences sp = getContext().getSharedPreferences(Constants.SP_NAME, 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(HOSPITAL_KEY, hospital.toString());
                    editor.putString(DEPARTMENT_KEY, department.toString());
                    editor.putString(PHONENUMBER_KEY, phonenumber.toString());
                    editor.apply();
                } else {
                    viewModel.logout();
                }
                break;
            case R.id.layout_account:
                if (isaccount) {
                    Log.d("wuxin", "change password========");
                    showChangePwdDialog();
                }
                break;
        }
    }

    private void showChangePwdDialog() {
        if (changePwdDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_change_pwd, null);
            View oldPwd = view.findViewById(R.id.old_password);
            View newPwd = view.findViewById(R.id.new_password);
            View confirmPwd = view.findViewById(R.id.confirm_password);
            ((TextView) oldPwd.findViewById(R.id.title)).setText(R.string.dialog_old_password);
            ((TextView) newPwd.findViewById(R.id.title)).setText(R.string.dialog_new_password);
            ((TextView) confirmPwd.findViewById(R.id.title)).setText(R.string.dialog_confirm_password);
            changePwdDialog = new AlertDialog.Builder(getActivity()).setView(view)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                        EditText etOldPwd = oldPwd.findViewById(R.id.edit);
                        EditText etNewPwd = newPwd.findViewById(R.id.edit);
                        EditText etConfirmPwd = confirmPwd.findViewById(R.id.edit);
                        changePassword(etOldPwd.getText().toString(), etNewPwd.getText().toString(), etConfirmPwd.getText().toString());
                        hideSoftInput(changePwdDialog.getButton(which));
                    }).setNegativeButton(R.string.cancel, (dialog, which) -> hideSoftInput(changePwdDialog.getButton(which))).create();
            changePwdDialog.getWindow().getDecorView().setOnTouchListener((v, event) -> hideSoftInput(changePwdDialog.getWindow().getDecorView()));
        }
        changePwdDialog.show();
    }

    private void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(requireContext(), "填写内容不可为空", Toast.LENGTH_SHORT).show();
            return;
        } else if (newPassword.equals(oldPassword)) {
            Toast.makeText(requireContext(), "新旧密码相同", Toast.LENGTH_SHORT).show();
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "新密码不相符", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.changePassword(oldPassword, newPassword);
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("wuxin", "===========" + mParam1);
        }
    }
}