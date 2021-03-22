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

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.fenix.spirometer.ble.Contants.MSG_DATA_RECEIVED;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECT;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_CONNECTING;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_DISCONNECT;
import static com.fenix.spirometer.ble.Contants.MSG_DEVICE_INITIALIZED;

public class BleDeviceClient {
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

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private final BluetoothGattCallback mGattCallback = new BaseBluetoothGattCallback();

    private BleDeviceClient() {
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

        if (thread == null) {
            thread = new HandlerThread("BleDeviceClient");
            thread.start();
        }
        mHandler = new MyHandler(thread.getLooper(), this);
        this.resultHandler = resultHandler;
        return true;
//      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//      context.startActivity(enableBtIntent);
    }

    // 以下为对外接口
    // 3.设备直连，不需要扫描过程。传入mac地址，来源应该是二维码扫描
    public void connectTo(String mac) {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
//        for (BluetoothDevice device : devices) {
//            Log.d("hff", "设备：" + device.getName() + " " + device.getAddress());
//        }
        mBluetoothDevice = btAdapter.getRemoteDevice(mac);
        Log.d("hff", "设备：" + mBluetoothDevice.getName() + " " + mBluetoothDevice.getAddress());
        mBluetoothGatt = mBluetoothDevice.connectGatt(context, false, mGattCallback);
        mBluetoothGatt.connect();
        Log.d("hff", "mBluetoothGatt = " + mBluetoothGatt);
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        Log.d("hff", "services: " + services);
        for (BluetoothGattService service : services) {
            Log.d("hff", "uuid: " + service.getUuid());
            List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : chars) {
                Log.d("hff", "characteristic.uuid: " + characteristic.getUuid());
            }

        }
    }

    // 4.开始测量
    public void startMeasure() {
        sendCommand(new byte[0]);
        dataCleaner = new DataCleaner();
    }

    // 5.结束测量
    public void stopMeasure() {
        sendCommand(new byte[0]);
    }

    private void sendCommand(byte[] command) {
        mWriteCharacteristic.setValue(command);
        mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (btAdapter == null || mBluetoothGatt == null) {
            return;
        }
        boolean isSuccess = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
//        if (UUID_BLE_SPP_NOTIFY.equals(characteristic.getUuid()) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            sendSuccess = bluetoothLeService.mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    // 6.设备断开
    public void disconnect() {
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
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
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    Log.d("hff", "uuid: " + service.getUuid());
                    List<BluetoothGattCharacteristic> chars = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : chars) {
                        Log.d("hff", "characteristic.uuid: " + characteristic.getUuid());
                    }

                }
                // 3.1连接成功，准备寻找服务。
//                try {
//                    Thread.sleep(1000);
//                    mBluetoothGatt.discoverServices();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                mHandler.sendEmptyMessage(MSG_DEVICE_CONNECT);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //断连发送消息
                mHandler.sendEmptyMessage(MSG_DEVICE_DISCONNECT);
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO：以下操作转给Handler处理
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService serviceRead;
                BluetoothGattService serviceWrite;
                serviceRead = gatt.getService(UUID.fromString(""));
                serviceWrite = gatt.getService(UUID.fromString(""));
                // 读
                if (serviceRead != null) {
                    mNotifyCharacteristic = serviceRead.getCharacteristic(UUID.fromString(""));
                }

                // 写
                if (serviceWrite != null) {
                    mWriteCharacteristic = serviceWrite.getCharacteristic(UUID.fromString(""));
                }
                // 打开通知开关（读）
                if (mNotifyCharacteristic != null) {
                    //设置可以接收通知，有点忘了
                }
            } else {
                //服务连接失败
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // 收到设备上报数据？？？？？
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //收到设备notify值 （设备上报值）
            }
        }

        //发送命令给设备成功之后
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //write成功
            }

        }

        //收到通知（read），？？？？？
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        //打开通知开关成功后会回调这里
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
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
            switch (msg.what) {
                case MSG_DEVICE_CONNECT:
                    // 连接成功
                    postMessage(MSG_DEVICE_CONNECT, null);
                    break;
                case MSG_DEVICE_DISCONNECT:
                    // 断开连接
                    postMessage(MSG_DEVICE_DISCONNECT, null);
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
                        postMessage(MSG_DEVICE_DISCONNECT, dealWithData((MeasureData) msg.obj));
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
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
            Message message = resultHandler.obtainMessage();
            message.obj = data;
            resultHandler.sendMessage(message);
        }
    }

    public void setResultHandler(Handler handler) {
        resultHandler = handler;
    }
}
