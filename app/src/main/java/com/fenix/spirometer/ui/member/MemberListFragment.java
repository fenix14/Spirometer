package com.fenix.spirometer.ui.member;

import androidx.annotation.LayoutRes;
import androidx.navigation.fragment.NavHostFragment;

import android.view.View;
import android.widget.LinearLayout;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MemberListFragment extends BaseVMFragment implements CustomExcel.OnRowStateChangeListener, CustomToolbar.OnItemClickListener {
    private CustomExcel<Member> excel;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(viewModel.isTesting());
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(R.string.tab_member_list));
        toolbar.setLeftText(viewModel.isTesting() ? getString(R.string.item_back) : null);
        toolbar.setRightText(viewModel.isTesting() ? null : getString(R.string.member_list_frag_title_right_add));
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(!viewModel.isTesting());
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.frag_members;
    }

    @Override
    protected void initView(View rootView) {
        excel = rootView.findViewById(R.id.customExcel);
        String[] headers = getResources().getStringArray(R.array.excel_title_member_list);

        List<Method> methodList = ModelUtils.getGetters(Member.class, new String[]{"getName", "getGender", "getAge", "getHeight", "getWeight", "getProvinceCity"});
        excel.setup(headers, new int[]{1, 4, 3, 3, 3, 3, 4, 2}, new ArrayList<>(), methodList);
        excel.setOnRowStateChangedListener(this);
    }

    @Override
    protected void initObserver() {
        viewModel.getAllMembers().observe(this, members -> excel.reload(members));
    }

    @Override
    public void onBindViewHolder(LinearLayout row, int position) {
        row.setOnClickListener(view -> {
            viewModel.setChosenMember(excel.getData(position));
            if (viewModel.isTesting()) {
                NavHostFragment.findNavController(this).navigateUp();
            } else {
                NavHostFragment.findNavController(this).navigate(R.id.members_to_member);
            }
        });
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        NavHostFragment.findNavController(this).navigate(R.id.members_to_member);
    }
}