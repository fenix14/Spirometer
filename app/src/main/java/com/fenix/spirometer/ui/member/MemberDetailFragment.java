package com.fenix.spirometer.ui.member;

import android.content.res.AssetManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.widget.CustomPreference;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加人员/填写测试人员
 */
public class MemberDetailFragment extends BaseVMPrefFragment implements CustomToolbar.OnItemClickListener, View.OnClickListener {
    private AlertDialog confirmCoverDialog;
    private static volatile boolean isUpdating = false;

    private CustomPreference spProvince;
    private CustomPreference spCity;
    private CustomPreference spCounty;

    private OptionsPickerView pvOptions;

    ArrayList<String> provinceBeanList = new ArrayList<>(); //  省份
    ArrayList<String> cities; //  城市
    ArrayList<List<String>> cityList = new ArrayList<>();
    ArrayList<String> district; //  区/县
    ArrayList<List<String>> districts;
    ArrayList<List<List<String>>> districtList = new ArrayList<>();

    @Override
    protected int getPrefId() {
        return R.xml.pref_member_detail;
    }

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(R.string.tab_member_detail));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(viewModel.isTesting() ? getString(R.string.choose) : null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        btmNav.setVisibility(View.VISIBLE);
        btmNav.setText(R.string.btn_save);
        btmNav.setOnClickListener(this);
        btmNav.setText(viewModel.isTesting() ? R.string.btn_next : R.string.btn_save);
    }

    @Override
    protected void initPreference() {
        spProvince = findPreference("setProvince");
        spCity = findPreference("setCity");
        spCounty = findPreference("setCounty");
    }

    @Override
    protected void initObserver() {
        viewModel.getAllMembers().observe(this, members -> {
            if (!isUpdating) {
                return;
            }
            if (members != null && members.contains(viewModel.getChosenMember())) {
                if (viewModel.isTesting()) {
                    NavHostFragment.findNavController(this).navigate(R.id.member_to_prepare);
                } else {
                    NavHostFragment.findNavController(this).navigateUp();
                }
            } else {
                Toast.makeText(getContext(), "人员信息更新失败", Toast.LENGTH_SHORT).show();
            }
            isUpdating = false;
        });
    }

    @Override
    protected void initData() {
        Member chosenMember = viewModel.getChosenMember();
        if (chosenMember != null) {
            CustomPreference preference = findPreference("setName");
            if (preference != null) {
                preference.setContent(chosenMember.getName());
            }

            preference = findPreference("setGender");
            if (preference != null) {
                preference.setContent(chosenMember.getGender().equals("男") ? 0 : 1);
            }

            preference = findPreference("setAge");
            if (preference != null) {
                preference.setContent(chosenMember.getAge());
            }

            preference = findPreference("setWeight");
            if (preference != null) {
                preference.setContent(chosenMember.getWeight());
            }

            preference = findPreference("setHeight");
            if (preference != null) {
                preference.setContent(chosenMember.getHeight());
            }

            preference = findPreference("setId");
            if (preference != null) {
                preference.setContent(chosenMember.getId());
            }

            preference = findPreference("setProvince");
            if (preference != null) {
                preference.setContent(chosenMember.getProvince());
            }

            preference = findPreference("setCity");
            if (preference != null) {
                preference.setContent(chosenMember.getCity());
            }

            preference = findPreference("setCounty");
            if (preference != null) {
                preference.setContent(chosenMember.getCounty());
            }

            preference = findPreference("setArea");
            if (preference != null) {
                preference.setContent(chosenMember.getArea());
            }
        }
        initJsonData();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if ("setProvince".equals(key) || "setCity".equals(key) || "setCounty".equals(key)) {
            showPicker();
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.footer) {
            return;
        }

        Member newMember = createModelFromPref(Member.class);
        if (newMember == null) {
            Toast.makeText(getActivity(), "新增失败", Toast.LENGTH_SHORT).show();
            return;
        }
        isUpdating = true;
        viewModel.insertOrUpdateMember(newMember);
    }

    private void showConfirmCoverDialog() {
        if (confirmCoverDialog == null) {
            confirmCoverDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.dialog_confirm_cover)
                    .setPositiveButton(R.string.dialog_ok, (comp, which) -> viewModel.updateMember(viewModel.getChosenMember()))
                    .setNegativeButton(R.string.cancel, null).create();
        }
        confirmCoverDialog.show();
    }

    @Override
    public void onLeftClick() {
        viewModel.setTesting(false);
        viewModel.setChosenMember(null);
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        NavHostFragment.findNavController(this).navigate(R.id.member_to_members);
    }

    private void showPicker() {
        if (pvOptions == null) {
            pvOptions = new OptionsPickerBuilder(getContext(), (options1, options2, options3, v) -> {
                spProvince.setContent(provinceBeanList.get(options1));
                spCity.setContent(cityList.get(options1).get(options2));
                spCounty.setContent(districtList.get(options1).get(options2).get(options3));
            }).setOptionsSelectChangeListener((options1, options2, options3) -> {
            }).setSubmitText("确定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleText("城市选择")//标题
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setCyclic(false, false, false)//循环与否
                    .setSelectOptions(0, 0, 0)  //设置默认选中项
                    .setOutSideCancelable(true)//点击外部dismiss default true
                    .isDialog(true)//是否显示为对话框样式
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .build();
            pvOptions.setPicker(provinceBeanList, cityList, districtList);//添加数据源
        }
        pvOptions.show();

    }

    private void initJsonData() {
        //解析数据
        String JsonData = getJson("provinces.json");//获取assets目录下的json文件数据
        parseJson(JsonData);
    }

    /**
     * 从asset目录下读取fileName文件内容
     *
     * @param fileName 待读取asset下的文件名
     * @return 得到省市县的String
     */
    private String getJson(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = getResources().getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 解析json填充集合
     *
     * @param str 待解析的json，获取省市县
     */
    public void parseJson(String str) {
        try {
            //  获取json中的数组
            JSONArray jsonArray = new JSONArray(str);
            //  遍历数据组
            for (int i = 0; i < jsonArray.length(); i++) {
                //  获取省份的对象
                JSONObject provinceObject = jsonArray.optJSONObject(i);
                //  获取省份名称放入集合
                String provinceName = provinceObject.getString("name");
//                provinceBeanList.add(new ProvinceBean(provinceName));
                provinceBeanList.add(provinceName);
                //  获取城市数组
                JSONArray cityArray = provinceObject.optJSONArray("city");
                cities = new ArrayList<>();
                //   声明存放城市的集合
                districts = new ArrayList<>();
                //声明存放区县集合的集合
                //  遍历城市数组
                for (int j = 0; j < cityArray.length(); j++) {
                    //  获取城市对象
                    JSONObject cityObject = cityArray.optJSONObject(j);
                    //  将城市放入集合
                    String cityName = cityObject.optString("name");
                    cities.add(cityName);
                    district = new ArrayList<>();
                    // 声明存放区县的集合
                    //  获取区县的数组
                    JSONArray areaArray = cityObject.optJSONArray("area");
                    //  遍历区县数组，获取到区县名称并放入集合
                    for (int k = 0; k < areaArray.length(); k++) {
                        String areaName = areaArray.getString(k);
                        district.add(areaName);
                    }
                    //  将区县的集合放入集合
                    districts.add(district);
                }
                //  将存放区县集合的集合放入集合
                districtList.add(districts);
                //  将存放城市的集合放入集合
                cityList.add(cities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}