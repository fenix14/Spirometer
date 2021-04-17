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
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.model.BleDeviceState.State;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static com.fenix.spirometer.ble.Contants.MSG_DATA_RECEIVED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECTED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECTING;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_DISCONNECTED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_DISCONNECTING;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_INITIALIZED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_STATE_CHANGE;
import static com.fenix.spirometer.ble.Contants.MSG_FAILED;
import static com.fenix.spirometer.ble.Contants.MSG_RAW_DATA_RECEIVED;
import static com.fenix.spirometer.ble.Contants.MSG_TESTING;
import static com.fenix.spirometer.ble.Contants.MSG_TEST_FINISH;

public class BleDeviceClient {
    private static final int KEY_PREFIX = 0;
    private static final int KEY_SUFFIX = 1;
    private static final int KEY_DATA_START = 2;
    private static final int KEY_DATA_STOP = 3;
    private static final SparseArray<byte[]> PACKET_FEATURES = new SparseArray<byte[]>() {{
        put(KEY_PREFIX, new byte[]{36, 70, 66, 36});
        put(KEY_SUFFIX, new byte[]{36, 70, 69, 36});
        put(KEY_DATA_START, new byte[]{49, 50});
        put(KEY_DATA_STOP, new byte[]{49, 51});
    }};
    private static final UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_COMMUNICATE = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final byte[] COMMAND_START_MEASURE = DataUtils.hexStringToBytes("FBFB000B0000FEFE");
    private static final byte[] COMMAND_STOP_MEASURE = DataUtils.hexStringToBytes("FBFB000D0000FEFE");
    private static final byte[] REC_DATA_HEADER = DataUtils.hexStringToBytes("FBFB000C");
    private static final byte[] REC_MEASURE_STOP = DataUtils.hexStringToBytes("FBFB000E");
    private static final byte[] REC_TAIL = DataUtils.hexStringToBytes("FEFE");

    public Context context;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private boolean isDiscovering = false;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private HandlerThread thread;
    private final MyHandler mHandler;
    private Handler resultHandler;
    private DataCleaner dataCleaner;
    BleDeviceState bleDeviceState = new BleDeviceState();
    StringBuilder data;
    private static volatile boolean isStoppingForStart = false;

    private BluetoothGattCharacteristic mCommunicateChara;
    private final BluetoothGattCallback mGattCallback = new BaseBluetoothGattCallback();

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
    public boolean init(Context context, Handler resultHandler) {
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

        Log.d("hff", "BleDeviceClient init success");
        setResultHandler(resultHandler);
        return true;
    }

    // 以下为对外接口
    // 3.设备直连，不需要扫描过程。传入mac地址，来源应该是二维码扫描
    public void connectTo(String mac) {
        mHandler.post(() -> {
            mBluetoothDevice = btAdapter.getRemoteDevice(mac);
            Log.d("hff", "设备：" + mBluetoothDevice.getName() + " " + mBluetoothDevice.getAddress());
            if (mBluetoothDevice == null) {
                mHandler.deliverMessage(MSG_FAILED, "BluetoothDevice not found");
                return;
            }
            mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, mGattCallback);
            if (mBluetoothGatt == null) {
                mHandler.deliverMessage(MSG_FAILED, "BluetoothGatt not found");
                return;
            }
            mBluetoothGatt.connect();
            bleDeviceState.setMac(mac);
            bleDeviceState.setDeviceName(mBluetoothDevice.getName());
            mHandler.deliverMessage(MSG_DEVICE_CONNECTING, null);
        });
    }

    // 4.开始测量
    public void startMeasure() {
        Log.d("hff", "startMeasure");
        if (bleDeviceState.getState() == State.STATE_READY) {
            if (!isStoppingForStart) {
                stopMeasure();
                isStoppingForStart = true;
                dataCleaner = new DataCleaner();
            }
        }
    }

    int time = 0;
    int preData = 0;
    Thread thread1;

    private void startFakeMeasure() {
        mHandler.deliverMessage(MSG_TESTING, null);
        thread1 = new Thread(() -> {
            while (time++ < 60) {
                int[] data = new int[100];
                for (int i = 0; i < 100; i++) {
                    boolean isAdd = (int) (Math.random() * 10) < 5;
                    if (preData == 0) {
                        data[i] = preData + 1;
                    } else {
                        data[i] = isAdd ? preData + 1 : preData - 1;
                    }
                    preData = data[i];
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.deliverMessage(MSG_DATA_RECEIVED, new MeasureData(System.currentTimeMillis(), data));
            }
            mHandler.deliverMessage(MSG_TEST_FINISH, null);
        });
        thread1.start();
    }

    private void startMeasureInternal(long delay) {
        isStoppingForStart = false;
        mHandler.postDelayed(() -> sendCommand(COMMAND_START_MEASURE), delay);
    }

    // 5.结束测量
    public void stopMeasure() {
        if (isStoppingForStart) {
            return;
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
        mHandler.deliverMessage(MSG_DEVICE_DISCONNECTING, null);
        mBluetoothGatt.disconnect();
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
                mHandler.deliverMessage(MSG_DEVICE_CONNECTED, null);
                // 3.1连接成功，准备寻找服务。
                try {
                    Thread.sleep(1000);
                    mBluetoothGatt.discoverServices();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mHandler.deliverMessage(MSG_DEVICE_DISCONNECTED, null);
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
                    mHandler.deliverMessage(MSG_FAILED, "Service not found.");
                    return;
                }
                mCommunicateChara = service.getCharacteristic(UUID_COMMUNICATE);
                if (mCommunicateChara == null) {
                    mHandler.deliverMessage(MSG_FAILED, "Characteristic not found.");
                    return;
                }
                // 打开通知开关（读）
                setCharacteristicNotification(mCommunicateChara, true);
            } else {
                //服务连接失败
                mHandler.deliverMessage(MSG_FAILED, "Service not found.");
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
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //write成功
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            // 处理数据
            Log.d("hff", "onCharacteristicChanged");
            mHandler.deliverMessage(MSG_RAW_DATA_RECEIVED, characteristic.getValue());
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
                mHandler.deliverMessage(MSG_DEVICE_INITIALIZED, null);
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

    private static class MyHandler extends Handler {
        private final WeakReference<BleDeviceClient> reference;

        MyHandler(Looper looper, BleDeviceClient client) {
            super(looper);
            reference = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            // TODO 空指针隐患处理
            if (reference == null || reference.get() == null) {
                return;
            }

            BleDeviceState state = reference.get().bleDeviceState;
            switch (msg.what) {
                case State.STATE_CONNECTING:
                    // 连接中
                    state.setState(State.STATE_CONNECTING);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    break;
                case MSG_DEVICE_DISCONNECTED:
                    // 断开连接
                    state.setState(State.STATE_DISCONNECTED);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    break;
                case MSG_DEVICE_DISCONNECTING:
                    // 断开连接
                    state.setState(State.STATE_DISCONNECTING);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    break;
                case MSG_DEVICE_CONNECTED:
                    // 连接成功
                    state.setState(State.STATE_CONNECTED);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    break;
                case MSG_DEVICE_INITIALIZED:
                    // 初始化完成
                    state.setState(State.STATE_READY);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    break;
                case MSG_TESTING:
                    // 初始化完成
                    state.setState(State.STATE_TESTING);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    break;
                case MSG_TEST_FINISH:
                    // 初始化完成
                    state.setState(State.STATE_FINISHED);
                    reference.get().postMessage(MSG_DEVICE_STATE_CHANGE, state);
                    state.setState(State.STATE_READY);
                    deliverMessage(MSG_DEVICE_INITIALIZED, state);
                    break;
                case MSG_RAW_DATA_RECEIVED:
                    // 数据接收
                    if (msg.obj instanceof byte[]) {
                        reference.get().dealWitRawData((byte[]) msg.obj);
                    }
                    break;
                case MSG_DATA_RECEIVED:
                    // 数据接收
                    if (msg.obj instanceof MeasureData) {
                        Log.d("hff", "handleMessage: " + msg.what + ": " + Arrays.toString(((MeasureData) msg.obj).voltages));
                        reference.get().postMessage(MSG_DATA_RECEIVED, msg.obj);
                    }
                    break;
                case MSG_FAILED:
                    reference.get().postMessage(MSG_FAILED, msg.obj);
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

    private synchronized void postMessage(int flag, @Nullable Object data) {
        if (resultHandler == null) {
            return;
        }
        if (data == null) {
            resultHandler.sendEmptyMessage(flag);
        } else {
            Message message = resultHandler.obtainMessage(flag);
            message.obj = data;
            resultHandler.sendMessage(message);
            Log.d("hff", "posting message, handler name = " + ((BleRepository.MyHandler) resultHandler).name);
        }
    }

    public void setResultHandler(Handler handler) {
        resultHandler = handler;
    }

    private static final int OFFSET_4 = 4;
    private static final int HEX_CODE = 0xffff;
    private static final int HEX_CODE_SIG = 0x00ff;
    private static final int DATA_SIZE_SINGLE_TRANS = 10;
    private MeasureData measureData;
    private int dataCount = 0;

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
                startMeasureInternal(1000);
            } else {
                if (measureData != null) {
                    // 处理因不足10条尚未发送的数据
                    mHandler.deliverMessage(MSG_DATA_RECEIVED, measureData);
                    measureData = null;
                    dataCount = 0;
                }
                mHandler.deliverMessage(MSG_TEST_FINISH, null);
            }
            return;
        }

        // 收到测量数据
        if (Arrays.equals(REC_DATA_HEADER, data)) {
            if (measureData == null) {
                measureData = new MeasureData();
                measureData.timeStamp = System.currentTimeMillis();
                measureData.voltages = new int[10];
            }
            // 电流转流量
            int voltage = ((newData[8] << 8) & HEX_CODE) + (newData[9] & HEX_CODE_SIG);
            Log.d("hff", "voltage = " + voltage + ", flow = " + DataUtils.voltageToFlow(voltage));
            measureData.voltages[dataCount++] = DataUtils.voltageToFlow(voltage);
        }

        // 每收集到10个数据传输
        if (dataCount == DATA_SIZE_SINGLE_TRANS) {
            mHandler.deliverMessage(MSG_DATA_RECEIVED, measureData);
            measureData = null;
            dataCount = 0;
        }
    }

    public interface BleStateListener {
        void onBleStateChanged(@State int state, Object data);
    }

    public interface BleDataListener {
        void onBleDataReceived(Object data);
    }
}
