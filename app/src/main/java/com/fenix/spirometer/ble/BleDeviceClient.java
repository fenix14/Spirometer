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
import androidx.annotation.Nullable;

import com.fenix.spirometer.model.BleDeviceState;
import com.fenix.spirometer.model.BleDeviceState.State;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static com.fenix.spirometer.ble.Contants.MSG_DATA_RECEIVED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECTED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECTING;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_DISCONNECTED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_DISCONNECTING;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_INITIALIZED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_STATE_CHANGE;
import static com.fenix.spirometer.ble.Contants.MSG_FAILED;

public class BleDeviceClient {
    private static final UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_COMMUNICATE = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final String COMMAND_START_MEASURE = "$FB$01110000$YE$";
    private static final char[] COMMAND_START_MEASURE1 = new char[]{'$', 'F', 'B', '$', '0', '1', '1', '1', '0', '0', '0', '0', '$', 'Y', 'E', '$'};

    String start;

    public Context context;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private boolean isDiscovering = false;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private HandlerThread thread;
    private MyHandler mHandler;
    private Handler resultHandler;
    private DataCleaner dataCleaner;
    private BleDeviceState bleDeviceState = new BleDeviceState();

    private BluetoothGattCharacteristic mCommunicateChara;
    private final BluetoothGattCallback mGattCallback = new BaseBluetoothGattCallback();

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

        if (thread == null) {
            thread = new HandlerThread("BleDeviceClient");
            thread.start();
        }
        mHandler = new MyHandler(thread.getLooper(), this);
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
            mBluetoothGatt.connect();
            bleDeviceState.setMac(mac);
            bleDeviceState.setDeviceName(mBluetoothDevice.getName());
            mHandler.deliverMessage(MSG_DEVICE_CONNECTING, null);
        });
    }

    // 4.开始测量
    public void startMeasure() {
        Log.d("hff", "startMeasure: " + Arrays.toString(COMMAND_START_MEASURE1));
        sendCommand(getBytes(COMMAND_START_MEASURE1));
        dataCleaner = new DataCleaner();
    }

    // 5.结束测量
    public void stopMeasure() {
        sendCommand(new byte[0]);
    }

    private void sendCommand(byte[] command) {
        Log.d("hff", "onCharacteristicChanged: " + Arrays.toString(command));
        mCommunicateChara.setValue(COMMAND_START_MEASURE);
        mBluetoothGatt.writeCharacteristic(mCommunicateChara);
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
            // 收到设备上报数据？？？？？
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //收到设备notify值 （设备上报值）
            }
        }

        //发送命令给设备成功之后
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("hff", "onCharacteristicWrite: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //write成功
            }

        }

        //收到通知（read），？？？？？
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d("hff", "onCharacteristicChanged: " + Arrays.toString(characteristic.getValue()));
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

    private class MyHandler extends Handler {
        private WeakReference<BleDeviceClient> reference;

        MyHandler(Looper looper, BleDeviceClient client) {
            super(looper);
            reference = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            // TODO 空指针隐患处理

            Log.d("hff", "handleMessage: " + msg.what + ": " + msg.obj);
            switch (msg.what) {
                case State.STATE_CONNECTING:
                    // 连接中
                    bleDeviceState.setState(State.STATE_CONNECTING);
                    postMessage(MSG_DEVICE_STATE_CHANGE, bleDeviceState);
                    break;
                case MSG_DEVICE_DISCONNECTED:
                    // 断开连接
                    bleDeviceState.setState(State.STATE_DISCONNECTED);
                    postMessage(MSG_DEVICE_STATE_CHANGE, bleDeviceState);
                    break;
                case MSG_DEVICE_DISCONNECTING:
                    // 断开连接
                    bleDeviceState.setState(State.STATE_DISCONNECTING);
                    postMessage(MSG_DEVICE_STATE_CHANGE, bleDeviceState);
                    break;
                case MSG_DEVICE_CONNECTED:
                    // 连接成功
                    bleDeviceState.setState(State.STATE_CONNECTED);
                    postMessage(MSG_DEVICE_STATE_CHANGE, bleDeviceState);
                    break;
                case MSG_DEVICE_INITIALIZED:
                    // 初始化完成
                    bleDeviceState.setState(State.STATE_READY);
                    postMessage(MSG_DEVICE_STATE_CHANGE, bleDeviceState);
                    break;
                case MSG_DATA_RECEIVED:
                    // 数据接收
                    if (msg.obj instanceof MeasureData) {
                        postMessage(MSG_DEVICE_DISCONNECTED, dealWithData((MeasureData) msg.obj));
                    }
                    break;
                case MSG_FAILED:
                    postMessage(MSG_FAILED, msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

        void deliverMessage(int what, Object message) {
            if (message != null) {
                Message msg = obtainMessage(what);
                sendMessage(msg);
            } else {
                sendEmptyMessage(what);
            }

        }
    }

    private synchronized MeasureData dealWithData(MeasureData rawData) {
        if (dataCleaner == null) {
            dataCleaner = new DataCleaner();
        }
        return dataCleaner.clean(rawData);
    }

    private synchronized void postMessage(int flag, @Nullable Object data) {
        if (data == null) {
            resultHandler.sendEmptyMessage(flag);
        } else {
            Message message = resultHandler.obtainMessage(flag);
            message.obj = data;
            resultHandler.sendMessage(message);
        }
    }

    public void setResultHandler(Handler handler) {
        resultHandler = handler;
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }
}
