package ztbsuper.lousysterm.services;

import android.bluetooth.BluetoothDevice;

public interface BluetoothServiceInterface {

    BluetoothServiceInterface getInstance();

    void setConnectionListener(ConnectionListener listener);

    void setDataReceiveListener(DataReceiveListener listener);

    void setStateChangeListener(StateChangeListener listener);

    void connect(BluetoothDevice device);

    void disconnect();

    boolean isConnected();

}
