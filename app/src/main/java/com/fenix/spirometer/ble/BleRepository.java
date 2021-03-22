package com.fenix.spirometer.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.fenix.spirometer.app.MyApplication;
import com.fenix.spirometer.model.BleDeviceState;

import static com.fenix.spirometer.ble.Contants.MSG_DATA_RECEIVED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECT;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECTING;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_DISCONNECT;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_INITIALIZED;

public class BleRepository {
    private static final BleRepository INSTANCE = new BleRepository();

    private BleDeviceClient bleDeviceClient;

    private HandlerThread handlerThread;

    private Handler repoHandler;

    private final MutableLiveData<BleDeviceState> mdBleDeviceState = new MutableLiveData<>();

    private final MutableLiveData<MeasureData> mdMeasureData = new MutableLiveData<>();

    private BleRepository() {
        handlerThread = new HandlerThread("BleRepository");
        handlerThread.start();
        repoHandler = new MyHandler(handlerThread.getLooper());

        bleDeviceClient = BleDeviceClient.getInstance();
        bleDeviceClient.init(MyApplication.getInstance(), repoHandler);
    }


    public static BleRepository getInstance() {
        return INSTANCE;
    }

    public MutableLiveData<BleDeviceState> getBleDeviceState() {
        return mdBleDeviceState;
    }

    public MutableLiveData<MeasureData> getMeasureData() {
        return mdMeasureData;
    }

    public void connectTo(@NonNull String mac) {
        BleDeviceState state = mdBleDeviceState.getValue();
        String currentMac = state == null ? null : state.getMac();
        if (mac.equals(currentMac)) {
            Log.w("hff", "same device connected: " + currentMac);
            return;
        }
        bleDeviceClient.connectTo(mac);
    }

    public void disConnect() {
        BleDeviceState state = mdBleDeviceState.getValue();
        String currentMac = state == null ? null : state.getMac();
        if (currentMac == null || !state.isConnect()) {
            Log.w("hff", "Already disconnected: " + currentMac);
            return;
        }
        bleDeviceClient.disconnect();
    }

    public void startMeasure() {
        bleDeviceClient.startMeasure();
    }

    public void stopMeasure() {
        bleDeviceClient.stopMeasure();
    }

    public void release(Context context) {
        handlerThread.getLooper().quitSafely();
        repoHandler.removeCallbacksAndMessages(null);
        bleDeviceClient.release();
    }

    private synchronized void postData(MeasureData data) {
        mdMeasureData.postValue(data);
    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_DEVICE_CONNECT:
                    // 连接成功
                    break;
                case MSG_DEVICE_DISCONNECT:
                    // 断开连接
                    break;
                case MSG_DEVICE_CONNECTING:
                    // 连接中
                    break;
                case MSG_DEVICE_INITIALIZED:
                    // 初始化完成
                    break;
                case MSG_DATA_RECEIVED:
                    // 数据接收
                    if (msg.obj instanceof MeasureData) {
                        postData((MeasureData) msg.obj);
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
