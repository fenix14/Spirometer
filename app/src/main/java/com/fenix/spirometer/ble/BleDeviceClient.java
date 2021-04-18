package com.fenix.spirometer.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fenix.spirometer.model.BleDeviceState.State;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.UUID;

import static com.fenix.spirometer.model.BleDeviceState.State.STATE_CONNECTED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_CONNECTING;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_DISCONNECTED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_DISCONNECTING;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_FAILED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_FINISHED;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_READY;
import static com.fenix.spirometer.model.BleDeviceState.State.STATE_TESTING;

public class BleDeviceClient {
    private static final UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_COMMUNICATE = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final byte[] COMMAND_START_MEASURE = DataUtils.hexStringToBytes("FBFB000B0000FEFE");
    private static final byte[] COMMAND_STOP_MEASURE = DataUtils.hexStringToBytes("FBFB000D0000FEFE");
    private static final byte[] REC_DATA_HEADER = DataUtils.hexStringToBytes("FBFB000C");
    private static final byte[] REC_MEASURE_STOP = DataUtils.hexStringToBytes("FBFB000E");

    private static final int OFFSET_4 = 4;
    private static final int HEX_CODE = 0xFFFF;
    private static final int HEX_CODE_SIG = 0x00FF;
    private static final int DATA_SIZE_SINGLE_TRANS = 10;

    public Context context;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private HandlerThread thread;
    private final MyHandler mHandler;

    private static volatile boolean isStoppingForStart = false;

    private MeasureData measureData;
    private int dataCount = 0;

    private BluetoothGattCharacteristic mCommunicateChara;
    private final BluetoothGattCallback mGattCallback = new BaseBluetoothGattCallback();

    private BleStateListener bleStateListener = new BleStateListener() {
        @Override
        public void onBleStateChanged(int state, Object data) {
        }

        @Override
        public void onBleDataReceived(int state, Object data) {
        }
    };

    private BleDeviceClient() {
        if (thread == null) {
            thread = new HandlerThread("BleDeviceClient");
            thread.start();
        }
        mHandler = new MyHandler(thread.getLooper(), this);
    }

    private static class InstanceHolder {
        private static BleDeviceClient INSTANCE = new BleDeviceClient();
    }

    // 1.获取单例
    public static BleDeviceClient getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // 2.初始化，传入上下文必须为Activity
    public boolean init(Context context, BleStateListener bleStateListener) {
        btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (btManager == null) {
            Log.w("hff", "BluetoothManager unavailable");
            return false;
        }
        btAdapter = btManager.getAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Log.w("hff", "BluetoothAdapter unavailable");
            return false;
        }

        if (bleStateListener != null) {
            this.bleStateListener = bleStateListener;
        }
        Log.d("hff", "BleDeviceClient init success");
        return true;
    }

    // 以下为对外接口
    // 3.设备直连，不需要扫描过程。传入mac地址，来源应该是二维码扫描
    public void connectTo(String mac) {
        mHandler.post(() -> {
            mBluetoothDevice = btAdapter.getRemoteDevice(mac);
            Log.d("hff", "设备：" + mBluetoothDevice.getName() + " " + mBluetoothDevice.getAddress());
            if (mBluetoothDevice == null) {
                mHandler.deliverMessage(STATE_FAILED, "BluetoothDevice not found");
                return;
            }
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, mGattCallback);
            if (mBluetoothGatt == null) {
                mHandler.deliverMessage(STATE_FAILED, "BluetoothGatt not found");
                return;
            }
            mBluetoothGatt.connect();
        });
    }

    // 4.开始测量
    public void startMeasure() {
        Log.d("hff", "startMeasure");
        if (!isStoppingForStart) {
            stopMeasure();
            isStoppingForStart = true;
        }
    }

    private void startMeasureInternal(long delay) {
        isStoppingForStart = false;
        mHandler.postDelayed(() -> sendCommand(COMMAND_START_MEASURE), delay);
    }

    // 5.结束测量
    public void stopMeasure() {
        if (isStoppingForStart) {
            isStoppingForStart = false;
        }
        sendCommand(COMMAND_STOP_MEASURE);
    }

    private void sendCommand(byte[] command) {
        Log.d("hff", "sendCommand: " + Arrays.toString(command));
        if (mCommunicateChara != null && mBluetoothGatt != null) {
            mCommunicateChara.setValue(command);
            mBluetoothGatt.writeCharacteristic(mCommunicateChara);
        }
    }

    // 6.设备断开
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mHandler.deliverMessage(STATE_DISCONNECTING, null);
            mBluetoothGatt.disconnect();
        }
    }

    private void closeGatt() {
    }

    // n.释放资源，必须调用，否则会有内存泄漏。
    public void release() {
        context = null;
        if (thread != null && thread.isAlive()) {
            thread.quitSafely();
            thread = null;
        }

        mBluetoothDevice = null;
        mBluetoothGatt = null;
        btAdapter = null;
        btManager = null;
    }

    private class BaseBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d("hff", "newState: " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mHandler.deliverMessage(STATE_CONNECTED, null);
                // 3.1连接成功，准备寻找服务。
                try {
                    Thread.sleep(1000);
                    mBluetoothGatt.discoverServices();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mHandler.deliverMessage(STATE_DISCONNECTED, null);
                //断连发送消息
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("hff", "onServicesDiscovered: " + status);
            // TODO：以下操作转给Handler处理
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID_SERVICE);
                if (service == null) {
                    mHandler.deliverMessage(STATE_FAILED, "Service not found.");
                    return;
                }
                mCommunicateChara = service.getCharacteristic(UUID_COMMUNICATE);
                if (mCommunicateChara == null) {
                    mHandler.deliverMessage(STATE_FAILED, "Characteristic not found.");
                    return;
                }
                // 打开通知开关（读）
                setCharacteristicNotification(mCommunicateChara, true);
            } else {
                //服务连接失败
                mHandler.deliverMessage(STATE_FAILED, "Service not found.");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("hff", "onCharacteristicRead: " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("hff", "onCharacteristicWrite: " + status + ", value = " + Arrays.toString(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            // 处理数据
            Log.d("hff", "onCharacteristicChanged");
            dealWitRawData(characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d("hff", "onDescriptorRead " + status);
        }

        //打开通知开关成功后会回调这里
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d("hff", "onDescriptorWrite: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mHandler.deliverMessage(STATE_READY, null);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d("hff", "onReadRemoteRssi " + status);
        }
    }

    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (btAdapter == null || mBluetoothGatt == null) {
            return;
        }
        boolean isSuccess = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        Log.d("hff", "setCharacteristicNotification: " + isSuccess);
        if (UUID_COMMUNICATE.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean result = mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    private synchronized void dealWitRawData(byte[] newData) {
        Log.d("hff", "newData = " + DataUtils.Bytes2HexString(newData));
        if (newData == null || newData.length < 8) {
            return;
        }
        int index = 0;
        byte[] data = DataUtils.subByteArray(newData, index, OFFSET_4);
        // 收到结束回执
        if (Arrays.equals(REC_MEASURE_STOP, data)) {
            if (isStoppingForStart) {
                switch (newData[6]) {
                    case 1:
                        startMeasureInternal(0);
                        break;
                    case 2:
                    case 0:
                    default:
                        break;
                }
            } else {
                if (measureData != null) {
                    // 处理因不足10条尚未发送的数据
                    mHandler.deliverMessage(STATE_TESTING, measureData);
                    measureData = null;
                    dataCount = 0;
                }
                mHandler.deliverMessage(STATE_FINISHED, null);
            }
            return;
        }

        // 收到测量数据
        if (Arrays.equals(REC_DATA_HEADER, data)) {
            if (measureData == null) {
                measureData = new MeasureData();
                measureData.timeStamp = System.currentTimeMillis();
                measureData.flow = new int[DATA_SIZE_SINGLE_TRANS];
            }
            // 电流转流量
            int voltage = ((newData[8] << 8) & HEX_CODE) + (newData[9] & HEX_CODE_SIG);
            Log.d("hff", "voltage = " + voltage + ", flow = " + DataUtils.voltageToFlow(voltage));
            measureData.flow[dataCount++] = DataUtils.voltageToFlow(voltage);
        }

        // 每收集到10个数据传输
        if (dataCount == DATA_SIZE_SINGLE_TRANS) {
            mHandler.deliverMessage(STATE_TESTING, measureData);
            measureData = null;
            dataCount = 0;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BleDeviceClient> reference;

        MyHandler(Looper looper, BleDeviceClient client) {
            super(looper);
            reference = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d("hff", "handleMesage: " + msg.what);
            // TODO 空指针隐患处理
            if (reference == null || reference.get() == null) {
                return;
            }
            switch (msg.what) {
                case STATE_CONNECTING: // 连接中
                case STATE_CONNECTED: // 连接成功
                case STATE_READY: // 初始化完成
                case STATE_FAILED:
                    reference.get().bleStateListener.onBleStateChanged(msg.what, msg.obj);
                    break;
                case STATE_DISCONNECTED: // 断开连接
                case STATE_DISCONNECTING:// 断开连接中
                    reference.get().bleStateListener.onBleStateChanged(msg.what, null);
                    break;
                case STATE_TESTING:
                    // 数据接收
                    if (msg.obj instanceof MeasureData) {
                        reference.get().bleStateListener.onBleDataReceived(STATE_TESTING, msg.obj);
                    }
                    break;
                case STATE_FINISHED:
                    // 初始化完成
                    reference.get().bleStateListener.onBleDataReceived(STATE_FINISHED, new MeasureData());
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

        synchronized void deliverMessage(int what, Object message) {
            if (message != null) {
                Message msg = obtainMessage();
                msg.what = what;
                msg.obj = message;
                sendMessage(msg);
            } else {
                sendEmptyMessage(what);
            }

        }
    }

    public interface BleStateListener {
        void onBleStateChanged(@State int devState, Object data);

        void onBleDataReceived(@State int testState, Object data);
    }
}
