package com.fenix.spirometer.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.ui.base.BaseVMFragment;
import com.fenix.spirometer.scanner.ScanActivity;
import com.fenix.spirometer.ui.widget.CustomToolbar;
import com.fenix.spirometer.util.Constants;
import com.fenix.spirometer.util.Utils;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * 首页：显示功能列表
 */
public class HomeFragment extends BaseVMFragment implements View.OnClickListener, CustomToolbar.OnItemClickListener {
    private boolean isConnect = false;
    private CustomToolbar toolbar;
    private AlertDialog btConnDialog;

    protected void initToolNavBar() {
        viewModel.setShowLightToolbar(false);
        toolbar = getToolbar();
        toolbar.clear();
        toolbar.setCenterText(null);
        toolbar.setLeftText(null);
        toolbar.setRightText(getString(R.string.ble_state_disconnect));
        toolbar.setOnItemClickListener(this);

        viewModel.setShowNavBar(true);
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.frag_home;
    }

    @Override
    protected void initView(View rootView) {
        TextView funcStandard = rootView.findViewById(R.id.tv_fuc_standard_test);
        funcStandard.setTag(Constants.TrainingType.STANDARD);
        funcStandard.setOnClickListener(this);

        TextView funcVitalCapacity = rootView.findViewById(R.id.tv_fuc_vital_capacity_training);
        funcVitalCapacity.setTag(Constants.TrainingType.VITAL_CAPACITY);
        funcVitalCapacity.setOnClickListener(this);

        TextView funcDiastolic = rootView.findViewById(R.id.tv_fuc_diastolic_test);
        funcDiastolic.setTag(Constants.TrainingType.DIASTOLIC);
        funcDiastolic.setOnClickListener(this);

        TextView funcPet = rootView.findViewById(R.id.tv_fuc_pet_test);
        funcPet.setTag(Constants.TrainingType.PET);
        funcPet.setOnClickListener(this);

        TextView funcVideo = rootView.findViewById(R.id.tv_fuc_operation_video);
        funcVideo.setOnClickListener(this);

        TextView funcHistory = rootView.findViewById(R.id.tv_fuc_history_report);
        funcHistory.setOnClickListener(this);
    }

    @Override
    protected void initObserver() {
        Log.d("hff", "initObserver");
        viewModel.getBleDeviceState().observe(this, bleDeviceState -> {
            Log.d("hff", "state = " + bleDeviceState);
            if (bleDeviceState != null) {
                int state = bleDeviceState.getState();
                switch (state) {
                    case BleDeviceState.State.STATE_CONNECTED:
                        isConnect = true;
                        toolbar.setRightText(getString(R.string.ble_state_connect));
                        break;
                    case BleDeviceState.State.STATE_CONNECTING:
                        toolbar.setRightText(getString(R.string.ble_state_connecting));
                        break;
                    case BleDeviceState.State.STATE_DISCONNECTED:
                        isConnect = false;
                        toolbar.setRightText(getString(R.string.ble_state_disconnect));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId();
        if (vId == R.id.tv_fuc_operation_video) {
            viewModel.stopMeasure();
            Toast.makeText(getActivity(), "go to Video!", Toast.LENGTH_SHORT).show();
        } else if (vId == R.id.tv_fuc_history_report) {
            NavHostFragment.findNavController(this).navigate(R.id.home_to_history);
        } else {
//            if (!isConnect) {
//                Toast.makeText(getActivity(), R.string.confirm_bt_connection, Toast.LENGTH_SHORT).show();
//                return;
//            }
//            viewModel.startMeasure();
            viewModel.setTesting(true);
            NavHostFragment.findNavController(this).navigate(R.id.home_to_member);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setTesting(false);
        viewModel.setChosenMember(null);
    }

    @Override
    public void onLeftClick() {
    }

    @Override
    public void onRightClick() {
        if (isConnect) {
            showBtConnectDialog(getString(R.string.confirm_replace_ble_device));
            return;
        }
        viewModel.connectToBleDevice("20:05:19:15:20:1E");
    }

    private void showBtConnectDialog(String content) {
        if (btConnDialog == null) {
            btConnDialog = Utils.createConfirmDialog(getActivity(), content,
                    (dialogInterface, which) -> {
                        if (which == BUTTON_POSITIVE) {
                            viewModel.disconnect();
                            startActivity(new Intent(getActivity(), ScanActivity.class));
                        }
                    });
        } else {
            btConnDialog.setMessage(content);
        }
        btConnDialog.show();
    }
}