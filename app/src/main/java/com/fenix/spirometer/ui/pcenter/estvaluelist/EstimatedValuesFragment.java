package com.fenix.spirometer.ui.pcenter.estvaluelist;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.EstValue;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EstimatedValuesFragment extends BaseVMFragment implements CustomExcel.OnRowStateChangeListener, CustomToolbar.OnItemClickListener {
    static final String KEY_EST_VALUE = "EstValue";
    private EstiValueViewModel estiValueViewModel;
    private CustomExcel<EstValue> excel;
    private Switch switchGender;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowNavBar(false);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(getString(R.string.tab_est_value_list));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_est_values;
    }

    @Override
    protected void initView(View rootView) {
        estiValueViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(EstiValueViewModel.class);

        switchGender = rootView.findViewById(R.id.switch_gender);
        switchGender.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            estiValueViewModel.loadEstValuesWithGender(!isChecked);
        });
        switchGender.setChecked(false);

        excel = rootView.findViewById(R.id.customExcel);
        String[] headers = getResources().getStringArray(R.array.excel_title_est_value_list);

        List<Method> methodList = ModelUtils.getGetters(EstValue.class, new String[]{"getName", "getFormula"});
        excel.setup(headers, new int[]{3, 8, 3}, new ArrayList<>(), methodList);
        excel.setOnRowStateChangedListener(this);
    }

    @Override
    protected void initObserver() {
        estiValueViewModel.subscribeToEstValues(this, estValues -> {
            excel.reload(estValues);
        });
    }

    @Override
    public void onBindViewHolder(LinearLayout row,  int position) {
        TextView suffix = row.findViewWithTag(CustomExcel.TAG_COLUMN_SUFFIX);
        suffix.setBackgroundResource(R.drawable.bg_button);
        suffix.setTextColor(getResources().getColor(R.color.white, null));
        suffix.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_EST_VALUE, excel.getData(position));
            NavHostFragment.findNavController(this).navigate(R.id.est_value_list_to_edit, bundle);
        });
        row.setOnClickListener(view -> {
            Toast.makeText(getContext(), "row clicked!!!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }

}
