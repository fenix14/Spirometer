package com.fenix.spirometer.ui.memberlist;

import androidx.navigation.Navigation;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Member;
import com.fenix.spirometer.ui.base.BaseToolbarVmFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MemberListFragment extends BaseToolbarVmFragment implements CustomExcel.OnRowStateChangeListener<Member> {
    private CustomExcel<Member>.BaseExcelAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_members;
    }

    @Override
    protected void initView(View rootView) {
        CustomExcel<Member> excel = rootView.findViewById(R.id.customExcel);
        String[] headers = getResources().getStringArray(R.array.excel_title_member_list);

        List<Method> methodList = Member.getOutputGetters(new String[]{"getName", "getGender", "getAge", "getHeight", "getWeight", "getProvinceCity"});
        adapter = excel.setup(headers, new int[]{1, 4, 3, 3, 3, 3, 4, 2}, new ArrayList<>(), methodList, true, true);
        adapter.setOnRowStateChangedListener(this);

    }

    @Override
    protected void initObserver() {
        viewModel.subscribeToMembers(this, members -> {
            adapter.reload(members);
        });
    }

    @Override
    public void onBindViewHolder(LinearLayout row, Member data, int position) {
        row.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "onClick:" + data.getName(), Toast.LENGTH_SHORT).show();
        });
    }
}