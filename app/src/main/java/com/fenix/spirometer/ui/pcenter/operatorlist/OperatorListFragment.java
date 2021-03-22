package com.fenix.spirometer.ui.pcenter.operatorlist;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.fenix.spirometer.ui.pcenter.operatorlist.OperatorDetailFragment.FLAG_DATA;

/**
 * 操作人员管理
 */
public class OperatorListFragment extends BaseVMFragment implements CustomExcel.OnRowStateChangeListener, CustomToolbar.OnItemClickListener {
    private AlertDialog delDialog;
    private OperatorViewModel operViewModel;
    private CustomExcel<Operator> excel;
    private Operator editOperator;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowNavBar(false);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(getString(R.string.tab_member_list));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(getString(R.string.operator_list_frag_title_right_add));
        toolbar.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_operators;
    }

    @Override
    protected void initView(View rootView) {
        operViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(OperatorViewModel.class);
        excel = rootView.findViewById(R.id.customExcel);
        String[] headers = getResources().getStringArray(R.array.excel_title_operator_list);

        List<Method> methodList = ModelUtils.getGetters(Operator.class, new String[]{"getDisplayName", "getUserId", "getPassword", "getDuty"});
        excel.setup(headers, new int[]{1, 4, 7, 5, 6, 2}, new ArrayList<>(), methodList);
        excel.setOnRowStateChangedListener(this);
    }

    @Override
    public void onBindViewHolder(LinearLayout row, int position) {
        row.setOnClickListener(view -> gotoDetailPage(excel.getData(position)));

        View suffix = row.findViewWithTag(CustomExcel.TAG_COLUMN_SUFFIX);
        if (excel.getData(position).isAdmin()) {
            // 禁止删除管理员
            suffix.setVisibility(View.INVISIBLE);
            return;
        }
        suffix.setOnClickListener(view -> {
            editOperator = excel.getData(position);
            showDeleteDialog();
        });
    }

    @Override
    protected void initObserver() {
        operViewModel.subscribeToOperators(this, operators -> {
            excel.reload(operators);
        });
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
        gotoDetailPage(null);
    }

    private void showDeleteDialog() {
        if (delDialog == null) {
            delDialog = new AlertDialog.Builder(getContext()).setMessage(R.string.confirm_delete)
                    .setPositiveButton(R.string.dialog_ok, (comp, which) -> operViewModel.deleteOperator(editOperator))
                    .setNegativeButton(R.string.cancel, null).create();
        }
        delDialog.show();
    }

    private void gotoDetailPage(@Nullable Operator operator) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(FLAG_DATA, operator);
        NavHostFragment.findNavController(this).navigate(R.id.operator_to_detail, bundle);
    }
}