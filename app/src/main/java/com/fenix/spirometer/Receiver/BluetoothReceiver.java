package com.fenix.spirometer.Receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fenix.spirometer.scanner.ScannerViewModel;
import com.fenix.spirometer.util.BlueToothHelper;

import static com.fenix.spirometer.scanner.ScanActivity.ACTION_INIT_DATA;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String btName = null;
        String btAddress = null;
        BlueToothHelper mBlueToothHelper = BlueToothHelper.from(context);;
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//        ScannerViewModel viewModel = new ViewModelProvider(this, new ViewModelProvider.
//                AndroidViewModelFactory(getActivity().get(ScannerViewModel.class);
        if(ACTION_INIT_DATA.equals(action)){//就收扫描数据
            btName = intent.getStringExtra("bluetoothName");
            btAddress = intent.getStringExtra("bluetoothMAC");
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                if(device.getAddress().equals(btAddress)){//如果要连接设备扫描处出来 直接匹配
                    try {
//                        mBluetoothAdapter.cancelDiscovery();
//                        dimissProgressDialog();
                       mBlueToothHelper.createBond(btAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
//                    mCurrStatus=STATUS.FREE;
//                    mHandler.sendEmptyMessage(MESSAGE_STATE_EXIST);
                }
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                //mBondedList.add(device.getAddress());
//                if(device.getAddress().equals(mac)){//如果设备已匹配  直接连接
//                    try {
//                        dimissProgressDialog();
//                        Log.d("wuxin","BOND_BONDED======"+mBluetoothAdapter.getRemoteDevice(mac));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //mOnSearchDeviceListener.onSearchCompleted(mBondedList, mNewList);
            //dimissProgressDialog();
        } else if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)){
            Log.d("wuxin","ACTION_PAIRING_REQUEST======");
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
            switch (device.getBondState()){
                case BluetoothDevice.BOND_BONDED:
                    Log.d("wuxin","蓝牙配对成功");
                  //  viewModel.setBleDeviceState(new BleDeviceState(true,btName,btAddress));
//                    mCurrStatus=STATUS.CONNECTED;
//                    mHandler.sendEmptyMessage(MESSAGE_STATE_CONNECT);
                    break;
                case BluetoothDevice.BOND_BONDING:
                    break;
                case BluetoothDevice.BOND_NONE:
//                    mHandler.sendEmptyMessage(MESSAGE_STATE_DISCONNECT);
//                    mCurrStatus=STATUS.FREE;
                    Log.d("wuxin","蓝牙已断开");
                    break;
            }
        }
    }
}
