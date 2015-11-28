package ztbsuper.lousysterm.services;

import android.bluetooth.BluetoothDevice;

import java.io.IOException;

import ztbsuper.lousysterm.enums.BluetoothDeviceStatus;

public interface BluetoothService {

    void setDataReceiveListener(DataReceiveListener listener);

    void setStateChangeListener(StateChangeListener listener);

    void connect(BluetoothDevice device);

    void disconnect();

    boolean isConnected();

    BluetoothDeviceStatus getStatus();

    void write(byte[] data) throws IOException;
}
