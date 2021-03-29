package com.fenix.spirometer.ble;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fenix.spirometer.ble.Contants.MSG_DATA_RECEIVED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_STATE_CHANGE;
import static com.fenix.spirometer.ble.Contants.MSG_FAILED;

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

        Log.w("hff", "BleRepository.newInstance() >> init bleClient");
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
        repoHandler.post(() -> {
            BleDeviceState state = mdBleDeviceState.getValue();
            String currentMac = state == null ? null : state.getMac();
            if (mac.equals(currentMac) && (state.getState() == BleDeviceState.State.STATE_CONNECTED
                    || state.getState() == BleDeviceState.State.STATE_CONNECTING)) {
                Log.w("hff", "same device connected: " + currentMac);
                return;
            }
            bleDeviceClient.connectTo(mac);
        });
    }

    public void disConnect() {
        repoHandler.post(() -> {
            BleDeviceState state = mdBleDeviceState.getValue();
            String currentMac = state == null ? null : state.getMac();
            if (currentMac == null || state.getState() == BleDeviceState.State.STATE_DISCONNECTED
                    || state.getState() == BleDeviceState.State.STATE_DISCONNECTING) {
                Log.w("hff", "Already disconnected or disconnecting: " + currentMac);
                return;
            }
            Log.w("hff", "disconnecting: " + currentMac);
            bleDeviceClient.disconnect();
        });
    }

    public void startMeasure() {
        repoHandler.post(() -> bleDeviceClient.startMeasure());
    }

    public void stopMeasure() {
        repoHandler.post(() -> bleDeviceClient.stopMeasure());
    }

    public void release(Context context) {
        handlerThread.getLooper().quitSafely();
        repoHandler.removeCallbacksAndMessages(null);
        bleDeviceClient.release();
    }

    private void postData(MeasureData data) {
        repoHandler.post(() -> mdMeasureData.postValue(data));
    }

    private class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d("hff", "repo.msg : " + msg.what + ", msg: " + msg.obj);
            switch (msg.what) {
                case MSG_DEVICE_STATE_CHANGE:
                    if (msg.obj instanceof BleDeviceState) {
                        changeState((BleDeviceState) msg.obj);
                    }
                    // 初始化完成
                    break;
                case MSG_DATA_RECEIVED:
                    // 数据接收
                    if (msg.obj instanceof MeasureData) {
                        postData((MeasureData) msg.obj);
                    }
                    break;
                case MSG_FAILED:
                    changeState(null);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private void changeState(BleDeviceState state) {
        Log.d("hff", "newState = " + state);
        mdBleDeviceState.postValue(state);
    }
}
