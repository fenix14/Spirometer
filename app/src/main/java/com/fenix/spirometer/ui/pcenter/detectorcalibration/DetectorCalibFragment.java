package com.fenix.spirometer.ui.pcenter.detectorcalibration;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.DetectorCompensation;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.ui.widget.CustomExcel;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.AllViewModelFactory;
import com.fenix.spirometer.util.ModelUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DetectorCalibFragment extends BaseVMFragment implements CustomExcel.OnRowStateChangeListener, CustomToolbar.OnItemClickListener {
    private static final int TAG_VOLTAGE_1_2 = 0;
    private static final int TAG_VOLTAGE_2_3 = 1;
    private static final int TAG_VOLTAGE_4_4 = 2;
    private static final int TAG_VOLTAGE_4_5 = 3;

    private DetectorCalibViewModel dcViewModel;
    private CustomExcel<DetectorCompensation> excelVoltage;
    private AlertDialog dialog;
    private DetectorCompensation editCompensation;

    @Override
    protected void initToolNavBar() {
        viewModel.setShowNavBar(false);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();

        toolbar.setBackgroundResource(R.color.colorPrimary);
        toolbar.setCenterText(getString(R.string.tab_detector_calibration));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_detector_celibration;
    }

    @Override
    protected void initView(View rootView) {
        dcViewModel = new ViewModelProvider(this, new AllViewModelFactory()).get(DetectorCalibViewModel.class);

        String[] headers = getResources().getStringArray(R.array.excel_title_detector_comp_list);
        List<Method> methodList = ModelUtils.getGetters(DetectorCompensation.class, new String[]{"getVoltageRange", "getCompensation"});
        int[] gravities = new int[]{1, 6, 4, 3};
        excelVoltage = rootView.findViewById(R.id.excel_voltage);
        excelVoltage.setup(headers, gravities, new ArrayList<>(), methodList);
        excelVoltage.setOnRowStateChangedListener(this);
    }

    @Override
    public void onBindViewHolder(LinearLayout row, int position) {
        TextView suffix = row.findViewWithTag(CustomExcel.TAG_COLUMN_SUFFIX);
        suffix.setBackgroundResource(R.drawable.bg_button);
        suffix.setTextColor(getResources().getColor(R.color.white, null));
        suffix.setOnClickListener(view -> {
            editCompensation = excelVoltage.getData(position);
            showDialog();
        });
    }

    @Override
    protected void initObserver() {
        dcViewModel.subscribeToEstValues(this, data -> {
            excelVoltage.reload(data);
        });
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {

    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(requireContext()).setView(R.layout.dialog_edit_text).setPositiveButton(R.string.dialog_ok, (dialogInterface, i) -> {
                EditText editText = dialog.findViewById(R.id.dialog_edit);
                float newCompensation = new BigDecimal(editText.getText().toString().trim()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                float ccc = Math.abs(editCompensation.getCompensation() - newCompensation);
                int result = Float.compare(ccc, 0.01f);
                if (result >= 0) {
                    editCompensation.setCompensation(newCompensation);
                    dcViewModel.updateCompensation(editCompensation);
                }
                editText.setText("");
                InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null)
                    manager.hideSoftInputFromWindow(editText.getWindowToken(), 2);
            }).setNegativeButton(getString(R.string.dialog_cancel), null).create();
        }
        dialog.show();
    }
}
