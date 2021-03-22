package com.fenix.spirometer.ui.member;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.City;
import com.fenix.spirometer.model.County;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.model.Province;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.widget.CustomPreference;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加人员/填写测试人员
 */
public class MemberDetailFragment extends BaseVMPrefFragment implements CustomToolbar.OnItemClickListener, View.OnClickListener {
    public static final String FLAG_MEMBER = "member";

    private MemberViewModel memberViewModel;
    private ArrayAdapter<Province> provAdapter;
    private ArrayAdapter<City> cityAdapter;
    private ArrayAdapter<County> couAdapter;
    private List<Province> provinces = new ArrayList<>();
    private int provinceIndex;
    private Member chosenMember;
    private AlertDialog confirmCoverDialog;
    private static volatile boolean isFinish = false;
    private static boolean isTesting = false;


    @Override
    protected int getPrefId() {
        return R.xml.pref_member_detail;
    }

    @Override
    protected void initToolNavBar() {
        isTesting = viewModel.isTesting();
        chosenMember = viewModel.getTestMember();

        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(getString(R.string.tab_member_detail));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(isTesting ? getString(R.string.choose) : null);
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(false);
        Button btmNav = getFooter();
        btmNav.setVisibility(View.VISIBLE);
        btmNav.setText(R.string.btn_save);
        btmNav.setOnClickListener(this);
        btmNav.setText(isTesting ? R.string.btn_next : R.string.btn_save);
    }

    CustomPreference spProvince;
    CustomPreference spCity;
    CustomPreference spCounty;

    @Override
    protected void initPreference() {
        memberViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(MemberViewModel.class);

        spProvince = findPreference("setProvince");
        spCity = findPreference("setCity");
        spCounty = findPreference("setCounty");

        if (spProvince == null || spCity == null || spCounty == null) {
            return;
        }

        provAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, provinces);
        cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        couAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<>());

        spProvince.setSpinnerAdapter(provAdapter, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                provinceIndex = position;
                List<City> cities = provinces.get(position).getCities();
                if (cities.isEmpty() || !City.EMPTY_CITY.equals(cities.get(0))) {
                    cities.add(0, City.EMPTY_CITY);
                }
                Log.d("hff", "cities = " + cities.size() + ": " + cities);
                cityAdapter.clear();
                cityAdapter.addAll(cities);
                cityAdapter.notifyDataSetChanged();
                ((Spinner) spCity.getContentView()).setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spCity.setSpinnerAdapter(cityAdapter, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                List<County> counties = provinces.get(provinceIndex).getCities().get(position).getCounties();
                if (counties.isEmpty() || !County.EMPTY_COUNTY.equals(counties.get(0))) {
                    counties.add(0, County.EMPTY_COUNTY);
                }
                Log.d("hff", "counties = " + counties.size() + ": " + counties);
                couAdapter.clear();
                couAdapter.addAll(counties);
                couAdapter.notifyDataSetChanged();
                ((Spinner) spCounty.getContentView()).setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spCounty.setSpinnerAdapter(couAdapter, null);
    }

    @Override
    protected void initObserver() {
        memberViewModel.subscribeToProvinces(this, provinces -> {
            if (provinces != null && provAdapter != null) {
                provinces.add(0, Province.EMPTY_PROVINCE);
                provAdapter.clear();
                provAdapter.addAll(provinces);
                provAdapter.notifyDataSetChanged();
                if (chosenMember != null) {
                    spProvince.setContent(chosenMember.getCity());
                    spCity.setContent(chosenMember.getCity());
                    spCounty.setContent(chosenMember.getCounty());
                }
            }
        });

        viewModel.subscribeToMembers(this, members -> {
            if (!isFinish) {
                return;
            }
            if (members != null && members.contains(chosenMember) && isFinish) {
                if (isTesting) {
                    NavHostFragment.findNavController(this).navigate(R.id.member_to_prepare);
                } else {
                    NavHostFragment.findNavController(this).navigateUp();
                }
            } else {
                Toast.makeText(getContext(), "人员信息写入失败", Toast.LENGTH_SHORT).show();
            }
            isFinish = false;
        });
    }

    @Override
    protected void initData() {
        if (chosenMember != null) {
            CustomPreference preference = findPreference("setName");
            if (preference != null) {
                preference.setContent(chosenMember.getName());
            }

            preference = findPreference("setGender");
            if (preference != null) {
                preference.setContent(chosenMember.getGender());
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

            preference = findPreference("setCellphone");
            if (preference != null) {
                preference.setContent(chosenMember.getCellphone());
            }
        }
    }

    private LiveData<Member> ldMember;

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
        LiveData<Member> ldMember = viewModel.getMember(newMember.getCellphone(), newMember.getName());
        ldMember.observe(this, member -> {
            if (isFinish) {
                return;
            }
            if (member == null) {
                chosenMember = newMember;
                viewModel.addMember(chosenMember);
                isFinish = true;
            } else if (!member.equals(newMember)) {
                chosenMember = newMember;
                showConfirmCoverDialog();
            }
        });
    }

    private void showConfirmCoverDialog() {
        if (confirmCoverDialog == null) {
            confirmCoverDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.dialog_confirm_cover)
                    .setPositiveButton(R.string.dialog_ok, (comp, which) -> {
                        viewModel.updateMember(chosenMember);
                        isFinish = true;
                    })
                    .setNegativeButton(R.string.cancel, null).create();
        }
        confirmCoverDialog.show();
    }

    @Override
    public void onLeftClick() {
        viewModel.setTesting(false);
        viewModel.setTestMember(null);
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        NavHostFragment.findNavController(this).navigate(R.id.member_to_members);
    }
}