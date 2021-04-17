package com.fenix.spirometer.model;

import androidx.annotation.IntDef;

import java.io.Serializable;

/**
 * 蓝牙状态模型
 */
public class BleDeviceState implements Serializable {
    @IntDef({State.STATE_NONE, State.STATE_CONNECTING, State.STATE_CONNECTED, State.STATE_DISCONNECTED, State.STATE_DISCONNECTING, State.STATE_READY, State.STATE_TESTING, State.STATE_FINISHED})
    public @interface State {
        int STATE_NONE = 0;
        int STATE_CONNECTING = 1;
        int STATE_CONNECTED = 2;
        int STATE_DISCONNECTED = 3;
        int STATE_DISCONNECTING = 4;
        int STATE_READY = 5;
        int STATE_TESTING = 6;
        int STATE_FINISHED = 7;
    }

    private String deviceName;

    private String mac;

    @State
    private int state = State.STATE_NONE;

    public BleDeviceState() {
    }

    public BleDeviceState(String deviceName, String mac, @State int state) {
        this.deviceName = deviceName;
        this.mac = mac;
        this.state = state;
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

    @State
    public int getState() {
        return state;
    }

    public String getStateString() {
        switch (state) {
            case State.STATE_CONNECTING:
                return "连接中";
            case State.STATE_READY:
            case State.STATE_TESTING:
            case State.STATE_FINISHED:
            case State.STATE_CONNECTED:
                return "已连接";
            case State.STATE_DISCONNECTED:
            case State.STATE_DISCONNECTING:
            case State.STATE_NONE:
            default:
                return "未连接";
        }
    }

    public void setState(@State int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "BleDeviceState{" +
                "deviceName='" + deviceName + '\'' +
                ", mac='" + mac + '\'' +
                ", state=" + state +
                '}';
    }
}
