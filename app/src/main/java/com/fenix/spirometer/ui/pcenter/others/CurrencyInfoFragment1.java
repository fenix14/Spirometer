package com.fenix.spirometer.ui.pcenter.others;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.fenix.spirometer.R;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.widget.CustomPreference;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Utils;

import java.util.HashMap;

public class CurrencyInfoFragment1 extends BaseVMPrefFragment implements CustomToolbar.OnItemClickListener {
    public static final String FLAG_CONTENT_TYPE = "content_type";
    private static final String CONTENT_TYPE_CONTACT = "contact_us";
    private static final String CONTENT_TYPE_VERSION = "version";
    private static final String CONTENT_TYPE_MAC = "mac";
    private static final String CONTENT_TYPE_ACCOUNT = "account";
    private static final HashMap<String, String> SUPPORT_CONTENT_TYPE = new HashMap<String, String>() {{
        put(CONTENT_TYPE_CONTACT, "联系我们");
        put(CONTENT_TYPE_VERSION, "版本信息");
        put(CONTENT_TYPE_MAC, "MAC地址");
        put(CONTENT_TYPE_ACCOUNT, "账号管理");
        put("empty", "");
    }};
    private String contentType;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowNavBar(true);
        Bundle bundle = getArguments();
        contentType = bundle == null ? "empty" : bundle.getString(FLAG_CONTENT_TYPE, "empty");

        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(SUPPORT_CONTENT_TYPE.get(contentType));
        toolbar.setLeftText("<");
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        if (contentType.equals(CONTENT_TYPE_ACCOUNT)) {
            Button btmNav = getFooter();
            btmNav.setVisibility(View.VISIBLE);
            btmNav.setText(R.string.pref_footer_logout);
            btmNav.setOnClickListener(this::onClick);
        }
    }

    @Override
    protected void initPreference() {
        CustomPreference row1 = findPreference(getString(R.string.key_pref_first_row));
        if (row1 == null) {
            row1 = createPreference(getString(R.string.key_pref_first_row));
        }
        CustomPreference row2 = findPreference(getString(R.string.key_pref_second_row));
        if (row2 == null) {
            row2 = createPreference(getString(R.string.key_pref_second_row));
        }

        // TODO：数据源：数据库 or raw
        if (contentType.equals(CONTENT_TYPE_CONTACT)) {
            // 联系我们
            row1.setTitle(R.string.pref_title_cellphone);
            row1.setContent(R.string.pref_content_cellphone);
            row2.setTitle(R.string.pref_title_email);
            row2.setContent(R.string.pref_content_email);
        } else if (contentType.equals(CONTENT_TYPE_VERSION)) {
            // 版本信息
            row1.setTitle(R.string.pref_title_version);
            row1.setContent(Utils.getAppVersionName());
            row2.setVisible(false);
        } else if (contentType.equals(CONTENT_TYPE_MAC)) {
            // mac地址
            row1.setTitle(R.string.pref_title_mac);
            row1.setContent("NULL"); // TODO
            row2.setVisible(false);
        } else if (contentType.equals(CONTENT_TYPE_ACCOUNT)) {
            // 账号管理
            row1.setTitle(R.string.account_title);
            row1.setContent(R.string.item_suffix_skip);
            TextView textView = row1.getContentView();
            textView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            row2.setVisible(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_pref_first_row))) {
            if (contentType.equals(CONTENT_TYPE_ACCOUNT)) {
                // 账号管理页面：修改密码
                changePassword();
            }
        } else {
            return super.onPreferenceTreeClick(preference);
        }
        return true;
    }

    //TODO
    private void changePassword() {
    }

    @Override
    protected int getPrefId() {
        return R.xml.pref_currency_info;
    }

    public void onClick(View view) {
//        if (view.getId() == R.id.footer) {
//            viewModel.logout();
//        }
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
    }

    private CustomPreference createPreference(String key) {
        CustomPreference preference = new CustomPreference(requireContext());
        preference.setLayoutResource(R.layout.widget_preference_row);
        preference.setKey(key);
        preference.setContentType(0);

        PreferenceScreen screen = findPreference(getString(R.string.key_pref_screen));
        if (screen == null) {
            Toast.makeText(getActivity(), "布局配置失败", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
        } else {
            screen.addPreference(preference);
        }
        return preference;
    }

    public static boolean isContentTypeSupported(String contentType) {
        return SUPPORT_CONTENT_TYPE.containsKey(contentType);
    }

}