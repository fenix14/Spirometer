package com.fenix.spirometer.ui.fuctionlist;

import android.content.Intent;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fenix.spirometer.R;
import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.ui.base.BaseToolbarVmFragment;
import com.fenix.spirometer.util.Constants;

/**
 * 首页：显示功能列表
 */
public class HomeFragment extends BaseToolbarVmFragment implements View.OnClickListener {
    private TextView funcStandard, funcVitalCapacity, funcDiastolic, history_report;
    private boolean isConnect = false;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_home;
    }

    @Override
    protected void initView(View rootView) {
        funcStandard = rootView.findViewById(R.id.tv_fuc_standard_test);
        funcStandard.setTag(Constants.TrainingType.STANDARD);
        funcStandard.setOnClickListener(this);

        funcVitalCapacity = rootView.findViewById(R.id.tv_fuc_vital_capacity_training);
        funcVitalCapacity.setTag(Constants.TrainingType.VITAL_CAPACITY);
        funcVitalCapacity.setOnClickListener(this);

        funcDiastolic = rootView.findViewById(R.id.tv_fuc_diastolic_test);
        funcDiastolic.setTag(Constants.TrainingType.DIASTOLIC);
        funcDiastolic.setOnClickListener(this);

        history_report = rootView.findViewById(R.id.tv_fuc_history_report);
    }

    @Override
    protected void initObserver() {
        viewModel.subscribeToBleDeviceState(this, bleDeviceState -> {
            Log.d("hff", "Observer:isConnectTemp = " + bleDeviceState.isConnect());
            isConnect = bleDeviceState != null && bleDeviceState.isConnect();
            getToolbar().setRightText(getString(isConnect ? R.string.ble_state_connect : R.string.ble_state_disconnect));
        });
    }

    @Override
    public void onClick(View view) {
//        Intent intent = new Intent(getActivity(), TrainingActivity.class);
//        @Constants.BgType int type;
//        switch (view.getId()) {
//            case R.id.tv_fuc_standard_test:
//            case R.id.tv_fuc_vital_capacity_training:
//            case R.id.tv_fuc_diastolic_test:
//                if (!isConnect) {
//                    Toast.makeText(getActivity(), "蓝牙未连接", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                type = (Integer) view.getTag();
//                intent.putExtra("training_type", type);
//                break;
//            case R.id.tv_fuc_history_report:
//            case R.id.tv_fuc_operation_video:
//            case R.id.tv_fuc_message:
//            default:
//                return;
//        }
//        startActivity(intent);
    }

    @Override
    protected void onClickToolbarRight(View view) {
        //TODO 蓝牙连接逻辑
        viewModel.setBleDeviceState(new BleDeviceState(!isConnect, "BLE DEVICE", "aa:bb:cc:dd:ee"));
    }
}