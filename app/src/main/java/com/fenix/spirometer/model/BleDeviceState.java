package com.fenix.spirometer.model;

/**
 * 蓝牙状态模型
 */
public class BleDeviceState {
    private boolean isConnect = false;

    private String deviceName;

    private String mac;

    public BleDeviceState(boolean isConnect, String deviceName, String mac) {
        this.isConnect = isConnect;
        this.deviceName = deviceName;
        this.mac = mac;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
