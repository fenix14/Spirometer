package com.fenix.spirometer.ui.pcenter.others;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.model.Province;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.main.MainActivity;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.FileParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import static com.fenix.spirometer.util.Constants.SP_KEY_IS_INITIALIZED;

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
    boolean iscontact =false;
    TextView change_password;
    private final String HOSPITAL_KEY="hospital_key";
    private final String DEPARTMENT_KEY="department_key";
    private final String PHONENUMBER_KEY="phonenumber_key";

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
        mlayouthospital= rootView.findViewById(R.id.layout_hospital);
        mlayoutDepartment= rootView.findViewById(R.id.layout_Department);;
        mlayoutphone= rootView.findViewById(R.id.layout_phone);;
        mlayoutversion= rootView.findViewById(R.id.layout_version);;
        mlayoutaccount= rootView.findViewById(R.id.layout_account);;
        mexitButton = rootView.findViewById(R.id.button);
        change_password = (rootView).findViewById(R.id.change_password);
        mhospital=(rootView).findViewById(R.id.hospital_value);
        mdepartment=(rootView).findViewById(R.id.department_value);
        mphonenumber=(rootView).findViewById(R.id.phone_value);
        mexitButton.setOnClickListener(this);
    }

    @Override
    protected void initObserver() {

        if (mParam1.equals("contact")) {
            SharedPreferences sp = getContext().getSharedPreferences(Constants.SP_NAME, 0);
            mhospital.setText(sp.getString(HOSPITAL_KEY,""));
            mdepartment.setText(sp.getString(DEPARTMENT_KEY,""));
            mphonenumber.setText(sp.getString(PHONENUMBER_KEY,""));
            iscontact = true;
            toolbar.setCenterText("联系我们");
            mlayoutversion.setVisibility(View.GONE);
            mlayoutaccount.setVisibility(View.GONE);
            mexitButton.setText("保存");
        } else if (mParam1.equals("version")) {
            toolbar.setCenterText("版本信息");
            mlayouthospital.setVisibility(View.GONE);
            mlayoutDepartment.setVisibility(View.GONE);
            mlayoutphone.setVisibility(View.GONE);
            mlayoutaccount.setVisibility(View.GONE);
            mexitButton.setVisibility(View.GONE);
        }  else if (mParam1.equals("account")) {
            toolbar.setCenterText("账号管理");
            isaccount = true;
            mlayouthospital.setVisibility(View.GONE);
            mlayoutDepartment.setVisibility(View.GONE);
            mlayoutphone.setVisibility(View.GONE);
            mlayoutversion.setVisibility(View.GONE);
            change_password.setText(">");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                Log.d("wuxin", "exit========");
                if(iscontact){
                    Editable hospital=mhospital.getText();
                    Editable department=mdepartment.getText();
                    Editable phonenumber=mphonenumber.getText();
                    if(TextUtils.isEmpty(hospital)||TextUtils.isEmpty(department)||TextUtils.isEmpty(phonenumber)){
                        Toast.makeText(getContext(),R.string.pref_no_input_empyt,Toast.LENGTH_LONG).show();
                        return;
                    }
                    SharedPreferences sp = getContext().getSharedPreferences(Constants.SP_NAME, 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(HOSPITAL_KEY, hospital.toString());
                    editor.putString(DEPARTMENT_KEY, department.toString());
                    editor.putString(PHONENUMBER_KEY, phonenumber.toString());
                    editor.apply();
                }else{
                    viewModel.logout();
                }


                break;
            case R.id.layout_account:
                if (isaccount) {
                    Log.d("wuxin", "change password========");
                }
                break;
        }

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
            mParam1 = getArguments().getString("flag_deaults");
            Log.d("wuxin", "===========" + mParam1);
        }
    }
}