package ztbsuper.lousysterm.services;


import ztbsuper.lousysterm.enums.BluetoothEvent;

public interface StateChangeListener {
    void onConnectionStart(BluetoothEvent event);

    void onConnected(BluetoothEvent event);

    void onDisconnectionStart(BluetoothEvent event);

    void onDisconnected(BluetoothEvent event);

    void onDisconnectionFailed(BluetoothEvent event);

    void onConnectionFailed(BluetoothEvent event);
}
