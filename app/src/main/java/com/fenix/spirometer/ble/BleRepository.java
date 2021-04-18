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

import static com.fenix.spirometer.model.BleDeviceState.State.STATE_CONNECTED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_CONNECTING;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_DISCONNECTED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_FINISHED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_TESTING;

public class BleRepository implements BleDeviceClient.BleStateListener {
    private static final BleRepository INSTANCE = new BleRepository();

    private BleDeviceClient bleDeviceClient;

    private HandlerThread handlerThread;

    private Handler repoHandler;

    private BleDeviceClient.BleStateListener bleStateListener;

    private final MutableLiveData<BleDeviceState> mdBleDeviceState = new MutableLiveData<>();

    private final MutableLiveData<MeasureData> mdMeasureData = new MutableLiveData<>();

    private BleRepository() {
        handlerThread = new HandlerThread("BleRepository");
        handlerThread.start();
        repoHandler = new Handler(handlerThread.getLooper());
        bleDeviceClient = BleDeviceClient.getInstance();
        bleDeviceClient.init(MyApplication.getInstance(), this);
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
            if (state != null && mac.equals(state.getMac()) && (state.getState() == STATE_CONNECTED || state.getState() == STATE_CONNECTING)) {
                Log.w("hff", "same device ignore: " + mac);
                return;
            }
            state = new BleDeviceState();
            state.setMac(mac);
            state.setState(STATE_CONNECTING);
            postState(state);
            bleDeviceClient.connectTo(mac);
        });
    }

    public void reconnect() {
        repoHandler.post(() -> connectTo(mdBleDeviceState.getValue().getMac()));
    }
    public void disConnect() {
        repoHandler.post(() -> bleDeviceClient.disconnect());
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

    private void postState(BleDeviceState state) {
        repoHandler.post(() -> mdBleDeviceState.postValue(state));
    }

    private void postData(MeasureData data) {
        repoHandler.post(() -> mdMeasureData.postValue(data));
    }

    @Override
    public void onBleStateChanged(int devState, Object data) {
        Log.d("hff", "onBleStateChanged: " + devState);
        switch (devState) {
            case STATE_CONNECTED:
            case STATE_CONNECTING:
            case STATE_DISCONNECTED:
                BleDeviceState state = mdBleDeviceState.getValue();
                if (state == null) {
                    return;
                }
                state.setState(devState);
                if (data instanceof BluetoothDevice) {
                    state.setDeviceName(((BluetoothDevice) data).getName());
                }
                postState(state);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBleDataReceived(int testState, Object data) {
        if (testState == STATE_TESTING || testState == STATE_FINISHED) {
            if (data instanceof MeasureData) {
                postData((MeasureData) data);
            }
        }
    }
}
