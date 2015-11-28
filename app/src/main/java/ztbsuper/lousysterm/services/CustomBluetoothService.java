package ztbsuper.lousysterm.services;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;

import java.io.IOException;
import java.util.ArrayList;

import ztbsuper.lousysterm.enums.BluetoothEvents;

import static ztbsuper.lousysterm.util.LogUtils.debug;

public class CustomBluetoothService implements BluetoothServiceInterface {

    private BluetoothServiceInterface instance;
    private ArrayList<ConnectionListener> connectionListeners = new ArrayList<>();
    private ArrayList<DataReceiveListener> receiveListeners = new ArrayList<>();
    private ArrayList<StateChangeListener> stateChangeListeners = new ArrayList<>();
    private BluetoothSocket bluetoothSocket;
    private Handler handler;


    private CustomBluetoothService() {
    }

    @Override
    public BluetoothServiceInterface getInstance() {
        if (null == instance) {
            instance = new CustomBluetoothService();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case BluetoothEvents.CANNOT_CONNECT:
                            updateState(BluetoothEvents.CANNOT_CONNECT);
                            break;
                        case BluetoothEvents.CONNECTING:
                            updateState(BluetoothEvents.CONNECTING);
                            break;
                        case BluetoothEvents.CONNECTION_FAILED:
                            updateState(BluetoothEvents.CONNECTION_FAILED);
                            break;
                        case BluetoothEvents.CONNECTION_SUCCESS:
                            updateState(BluetoothEvents.CONNECTION_SUCCESS);
                            break;
                        case BluetoothEvents.DISCONNECTING:
                            updateState(BluetoothEvents.DISCONNECTING);
                            break;
                        case BluetoothEvents.DISCONNECTED:
                            updateState(BluetoothEvents.DISCONNECTED);
                            break;
                        case BluetoothEvents.DISCONNECTION_FAILED:
                            updateState(BluetoothEvents.DISCONNECTION_FAILED);
                            break;
                    }
                }
            };
        }
        return instance;
    }

    @Override
    public void setConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    @Override
    public void setDataReceiveListener(DataReceiveListener listener) {
        receiveListeners.add(listener);
    }

    @Override
    public void setStateChangeListener(StateChangeListener listener) {
        stateChangeListeners.add(listener);
    }

    @Override
    public void connect(BluetoothDevice device) {
        bluetoothSocket = null;
        DeviceConnectionThread deviceConnectionThread = new DeviceConnectionThread(device);
        new Thread(deviceConnectionThread).start();
    }

    @Override
    public void disconnect() {
        handler.obtainMessage(BluetoothEvents.DISCONNECTING);
        try {
            bluetoothSocket.close();
            handler.obtainMessage(BluetoothEvents.DISCONNECTION_FAILED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.obtainMessage(BluetoothEvents.DISCONNECTED);
    }

    @Override
    public boolean isConnected() {
        return null != bluetoothSocket && bluetoothSocket.isConnected();

    }

    private void updateState(int state) {
        for (StateChangeListener listener : stateChangeListeners) {
            listener.onStateChange(state);
        }
    }


    class DeviceConnectionThread implements Runnable {
        private BluetoothDevice device;

        public DeviceConnectionThread(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            handler.obtainMessage(BluetoothEvents.CONNECTING);
            try {
                ParcelUuid[] uuids = device.getUuids();
                for (ParcelUuid uuid : uuids) {
                    debug("device uuid: " + uuid.toString());
                }
                bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
            } catch (IOException e) {
                handler.obtainMessage(BluetoothEvents.CANNOT_CONNECT);
                e.printStackTrace();
            }
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                handler.obtainMessage(BluetoothEvents.CONNECTION_FAILED);
                e.printStackTrace();
            }
            handler.obtainMessage(BluetoothEvents.CONNECTION_SUCCESS, device);
        }
    }
}
