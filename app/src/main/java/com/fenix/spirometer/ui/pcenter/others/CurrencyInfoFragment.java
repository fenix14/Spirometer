package com.fenix.spirometer.ui.pcenter.others;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.main.MainActivity;
import com.fenix.spirometer.ui.widget.CustomToolbar;

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

public class CurrencyInfoFragment extends BaseVMFragment implements View.OnClickListener, CustomToolbar.OnItemClickListener {
    private String mParam1;
    CustomToolbar toolbar;
    LinearLayout mlayoutphone;
    LinearLayout mlayouemail;
    TextView mphonetitle;
    TextView mphonevaule;
    TextView memailtitle;
    TextView memailvaule;
    Button mexitButton;
    boolean isaccount = false;
    protected final int PERMS_REQUEST_CODE = 202;
    protected final int PERMS_BLUETOOTH_PRIVILEGED = 203;
    protected final int PERMS_LOCAL_MAC_ADDRESS = 204;

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
        mlayoutphone = rootView.findViewById(R.id.layout_phone);
        mlayouemail = rootView.findViewById(R.id.deaults_view_gone);
        mphonetitle = rootView.findViewById(R.id.deaults_title);
        mphonevaule = rootView.findViewById(R.id.deaults_value);
        memailtitle = rootView.findViewById(R.id.email_title);
        memailvaule = rootView.findViewById(R.id.email_value);
        mexitButton = rootView.findViewById(R.id.exitaccount);
        mexitButton.setOnClickListener(this);
        mlayouemail.setOnClickListener(this);
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.BLUETOOTH_PRIVILEGED
                , Manifest.permission.ACCESS_WIFI_STATE};
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(permissions, PERMS_REQUEST_CODE);
            requestPermissions(permissions, PERMS_BLUETOOTH_PRIVILEGED);
            requestPermissions(permissions, PERMS_LOCAL_MAC_ADDRESS);
        }
    }

    @Override
    protected void initObserver() {
        if (mParam1.equals("contact")) {
            toolbar.setCenterText("联系我们");
            mexitButton.setVisibility(View.GONE);
            mphonetitle.setText(R.string.pref_title_cellphone);
            mphonevaule.setText(R.string.pref_content_cellphone);
            memailtitle.setText(R.string.pref_title_email);
            memailvaule.setText(R.string.pref_content_email);
        } else if (mParam1.equals("version")) {
            toolbar.setCenterText("版本信息");
            mlayouemail.setVisibility(View.GONE);
            mexitButton.setVisibility(View.GONE);
            mphonetitle.setText(R.string.pref_title_version);
            mphonevaule.setText(R.string.pref_content_version);
        } else if (mParam1.equals("mac")) {
            toolbar.setCenterText("Mac地址");
            mlayouemail.setVisibility(View.GONE);
            mexitButton.setVisibility(View.GONE);
            mphonetitle.setText(R.string.pref_title_mac);
            mphonevaule.setText(getBtAddressByReflection());
        } else if (mParam1.equals("account")) {
            isaccount = true;
            mlayoutphone.setVisibility(View.GONE);
            memailtitle.setText(R.string.account_title);
            memailvaule.setText(">");
            toolbar.setCenterText("账号管理");
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exitaccount:
                Log.d("wuxin", "exit========");
                viewModel.logout();
                break;
            case R.id.deaults_view_gone:
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

    public static String getBtAddressByReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Field field = null;
        try {
            field = BluetoothAdapter.class.getDeclaredField("mService");
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                return "NULL";
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            if (method != null) {
                Object obj = method.invoke(bluetoothManagerService);
                if (obj != null) {
                    return obj.toString();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getWifiMacFromNode() {
        String wifiMac = "";
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile("/sys/class/net/wlan0/address", "r");
            f.seek(0);
            wifiMac = f.readLine().trim();
            f.close();
            Log.d("", "getWifiMacFromNode " + wifiMac);
            return wifiMac;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return wifiMac;
        } catch (IOException e) {
            e.printStackTrace();
            return wifiMac;
        } finally {
            if (f != null) {
                try {
                    f.close();
                    f = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}