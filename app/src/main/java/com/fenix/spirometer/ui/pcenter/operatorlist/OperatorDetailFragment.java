package com.fenix.spirometer.ui.pcenter.operatorlist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.Operator;
import com.fenix.spirometer.ui.base.BaseVMPrefFragment;
import com.fenix.spirometer.ui.widget.CustomPreference;
import com.fenix.spirometer.ui.widget.CustomToolbar;

public class OperatorDetailFragment extends BaseVMPrefFragment implements CustomToolbar.OnItemClickListener {
    public static final String FLAG_DATA = "operator";
    private Operator displayOperator;
    private AlertDialog confirmCoverDialog;
    private Operator newOperator;

    @Override
    protected void initToolNavBar() {
        Bundle data = getArguments();
        displayOperator = data == null ? null : (Operator) data.getSerializable(FLAG_DATA);

        viewModel.setShowLightToolbar(true);
        CustomToolbar toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(getString(displayOperator == null ? R.string.tab_operator_detail_add : R.string.tab_operator_detail));
        toolbar.setLeftText(getString(R.string.item_back));
        toolbar.setRightText(null);
        toolbar.setOnItemClickListener(this);

        if (displayOperator == null) {
            viewModel.setShowNavBar(false);
            Button btmNav = getFooter();
            btmNav.setVisibility(View.VISIBLE);
            btmNav.setText(R.string.btn_save);
            btmNav.setOnClickListener(this::onClick);
        }
    }

    @Override
    protected int getPrefId() {
        return R.xml.pref_operator_detail;
    }

    @Override
    protected void initPreference() {
        CustomPreference spDescription = findPreference("setDescription");
        EditText etDescription;
        if (spDescription != null && (etDescription = spDescription.getContentView()) != null) {
            etDescription.setMinLines(5);
        }
        if (displayOperator != null) {
            getPreferenceScreen().setEnabled(false);
        }
    }

    @Override
    protected void initData() {
        if (displayOperator != null) {
            CustomPreference preference;
            preference = findPreference("setUserId");
            if (preference != null) {
                preference.setContent(displayOperator.getUserId());
            }
            preference = findPreference("setPassword");
            if (preference != null) {
                preference.setContent(displayOperator.getPassword());
            }
            preference = findPreference("setDisplayName");
            if (preference != null) {
                preference.setContent(displayOperator.getDisplayName());
            }
            preference = findPreference("setDuty");
            if (preference != null) {
                preference.setContent(displayOperator.getDuty());
            }
            preference = findPreference("setDescription");
            if (preference != null) {
                preference.setContent(displayOperator.getDescription());
            }
        }
    }

    @Override
    public void onLeftClick() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onRightClick() {
    }

    private void onClick(View view) {
        if (view.getId() != R.id.footer) {
            return;
        }
        newOperator = createModelFromPref(Operator.class);
        if (newOperator == null) {
            Toast.makeText(getActivity(), "创建失败", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.getOperator(newOperator.getUserId()).observe(this, operator -> {
            if (operator != null) {
                showConfirmCoverDialog();
            } else {
                saveAndQuit();
            }
        });
    }

    private void showConfirmCoverDialog() {
        if (confirmCoverDialog == null) {
            confirmCoverDialog = new AlertDialog.Builder(requireContext()).setMessage(R.string.dialog_confirm_cover)
                    .setPositiveButton(R.string.dialog_ok, (comp, which) -> saveAndQuit())
                    .setNegativeButton(R.string.cancel, null).create();
        }
        confirmCoverDialog.show();
    }

    private void saveAndQuit() {
        viewModel.addOperator(newOperator);
        NavHostFragment.findNavController(this).navigateUp();
    }
}
