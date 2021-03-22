package com.fenix.spirometer.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BlueToothHelper {
    Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private static volatile com.fenix.spirometer.util.BlueToothHelper mBtHelperClient;
    private BluetoothSocket mBluetoothSocket;
    private String TAG="BlueToothHelper";

    public static com.fenix.spirometer.util.BlueToothHelper from(Context context) {
        if (mBtHelperClient == null) {
            synchronized (com.fenix.spirometer.util.BlueToothHelper.class) {
                if (mBtHelperClient == null)
                    mBtHelperClient = new com.fenix.spirometer.util.BlueToothHelper(context);
            }
        }
        return mBtHelperClient;
    }
    /**
     * private constructor for singleton
     *
     * @param context context
     */
    private BlueToothHelper(Context context) {
        mContext = context.getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice,
                          String str) throws Exception
    {
        try
        {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",
                    new Class[]
                            {byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                    new Object[]
                            {str.getBytes()});
            Log.e("returnValue", "" + returnValue);
        }
        catch (SecurityException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        return true;

    }

    public boolean removeBond(String mac)
            throws Exception
    {
        if (mac == null || TextUtils.isEmpty(mac))
            throw new IllegalArgumentException("mac address is null or empty!");
        if (!BluetoothAdapter.checkBluetoothAddress(mac))
            throw new IllegalArgumentException("mac address is not correct! make sure it's upper case!");
        mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
        Method removeBondMethod = remoteDevice.getClass().getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(remoteDevice);
        return returnValue.booleanValue();
    }
    public boolean createBond(String mac)
            throws Exception
    {    Log.d("wuxin","33333333");
        if (mac == null || TextUtils.isEmpty(mac))
            throw new IllegalArgumentException("mac address is null or empty!");
        if (!BluetoothAdapter.checkBluetoothAddress(mac))
            throw new IllegalArgumentException("mac address is not correct! make sure it's upper case!");
        mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
        Method createBondMethod = remoteDevice.getClass().getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(remoteDevice);
        return returnValue.booleanValue();
    }

    public void ConnectBuletooth(String mac) throws Exception {
        if (mac == null || TextUtils.isEmpty(mac))
            throw new IllegalArgumentException("mac address is null or empty!");
        if (!BluetoothAdapter.checkBluetoothAddress(mac))
            throw new IllegalArgumentException("mac address is not correct! make sure it's upper case!");
        mBluetoothAdapter.cancelDiscovery();
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
        mBluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.fromString("af71c0bb-c9c4-40a6-83db-eb3c0c8b2cda"));
        mBluetoothSocket.connect();
    };

    public void SearchBluetooth(){
        //showProgressDialog("正在扫描设备");
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    };
    public boolean isBond() {
        Set<BluetoothDevice> bondDevices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG,bondDevices.size() + "");
        if(bondDevices.size()>0){
            return true;
        }
//        for(BluetoothDevice bond : bondDevices){
////            //设备已经配对
////            Log.d(TAG,bond.getName());
////            if(bond.getAddress().equals(mac)){
////                return true;
////            }
////        }
        return false;
    }

    public void requestEnableBt() {
        if (mBluetoothAdapter == null) {
            throw new NullPointerException();
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }
}
