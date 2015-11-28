package ztbsuper.lousysterm.services;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ztbsuper.lousysterm.enums.BluetoothDeviceStatus;
import ztbsuper.lousysterm.enums.BluetoothEvent;
import ztbsuper.lousysterm.enums.BluetoothEventsSeq;

import static ztbsuper.lousysterm.util.LogUtils.debug;

public class CustomBluetoothService implements BluetoothService {

    private ArrayList<DataReceiveListener> receiveListeners = new ArrayList<>();
    private ArrayList<StateChangeListener> stateChangeListeners = new ArrayList<>();
    private BluetoothSocket bluetoothSocket;
    private Handler handler;
    private volatile BluetoothDeviceStatus deviceStatus = BluetoothDeviceStatus.DISCONNECT;
    private DeviceConnectedThread deviceConnectedThread;


    public CustomBluetoothService() {
        initHandlers();
    }


    private void initHandlers() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case BluetoothEventsSeq.CANNOT_CONNECT:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onConnectionFailed(BluetoothEvent.CANNOT_CONNECT);
                        }
                        break;
                    case BluetoothEventsSeq.CONNECTING:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onConnectionStart(BluetoothEvent.CONNECTING);
                        }
                        break;
                    case BluetoothEventsSeq.CONNECTION_FAILED:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onConnectionFailed(BluetoothEvent.CONNECTION_FAILED);
                        }
                        break;
                    case BluetoothEventsSeq.CONNECTION_SUCCESS:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onConnected(BluetoothEvent.CONNECTION_SUCCESS);
                        }
                        updateStatus(BluetoothDeviceStatus.CONNECTED);
                        deviceConnectedThread = new DeviceConnectedThread(bluetoothSocket);
                        new Thread(deviceConnectedThread).start();
                        break;
                    case BluetoothEventsSeq.DISCONNECTING:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onDisconnectionStart(BluetoothEvent.DISCONNECTING);
                        }
                        break;
                    case BluetoothEventsSeq.DISCONNECTED:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onDisconnected(BluetoothEvent.DISCONNECTED);
                        }
                        updateStatus(BluetoothDeviceStatus.DISCONNECT);
                        break;
                    case BluetoothEventsSeq.DISCONNECTION_FAILED:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onDisconnectionFailed(BluetoothEvent.DISCONNECTION_FAILED);
                        }
                        break;
                    case BluetoothEventsSeq.CONNECTION_LOST:
                        for (StateChangeListener listener : stateChangeListeners) {
                            listener.onDisconnected(BluetoothEvent.CONNECTION_LOST);
                        }
                    case BluetoothEventsSeq.READ_DATA:
                        for (DataReceiveListener listener : receiveListeners) {
                            listener.onDataReceive((Integer) msg.obj);
                        }
                    default:
                        break;
                }
            }
        };
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
    public synchronized void connect(BluetoothDevice device) {
        if (null == device) {
            return;
        }
        bluetoothSocket = null;
        DeviceConnectionThread deviceConnectionThread = new DeviceConnectionThread(device);
        new Thread(deviceConnectionThread).start();
    }

    @Override
    public synchronized void disconnect() {
        handler.obtainMessage(BluetoothEventsSeq.DISCONNECTING).sendToTarget();
        try {
            bluetoothSocket.close();
            handler.obtainMessage(BluetoothEventsSeq.DISCONNECTION_FAILED).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.obtainMessage(BluetoothEventsSeq.DISCONNECTED).sendToTarget();
    }

    @Override
    public boolean isConnected() {
        return null != bluetoothSocket && bluetoothSocket.isConnected();

    }

    @Override
    public void write(byte[] data) throws IOException {
        if (null != bluetoothSocket && bluetoothSocket.isConnected() && null != deviceConnectedThread) {
            synchronized (deviceConnectedThread) {
                deviceConnectedThread.write(data);
            }
        }
    }

    private synchronized void updateStatus(BluetoothDeviceStatus status) {
        this.deviceStatus = status;
    }


    class DeviceConnectionThread implements Runnable {
        private BluetoothDevice device;

        public DeviceConnectionThread(BluetoothDevice device) {
            this.device = device;
        }

        @Override
        public void run() {
            handler.obtainMessage(BluetoothEventsSeq.CONNECTING);
            try {
                ParcelUuid[] uuids = device.getUuids();
                for (ParcelUuid uuid : uuids) {
                    debug("device uuid: " + uuid.toString());
                }
                bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
            } catch (IOException e) {
                handler.obtainMessage(BluetoothEventsSeq.CANNOT_CONNECT).sendToTarget();
                e.printStackTrace();
            }
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                handler.obtainMessage(BluetoothEventsSeq.CONNECTION_FAILED).sendToTarget();
                e.printStackTrace();
            }
            handler.obtainMessage(BluetoothEventsSeq.CONNECTION_SUCCESS, device).sendToTarget();
        }
    }

    class DeviceConnectedThread implements Runnable {
        private final BluetoothSocket socket;
        private InputStream inputStream;
        private OutputStream outputStream;


        public DeviceConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (socket.isConnected()) {
                try {
                    int data = inputStream.read();
                    handler.obtainMessage(BluetoothEventsSeq.READ_DATA, data).sendToTarget();
                } catch (IOException e) {
                    handler.obtainMessage(BluetoothEventsSeq.CONNECTION_LOST);
                    e.printStackTrace();
                }
            }
            handler.obtainMessage(BluetoothEventsSeq.DISCONNECTED).sendToTarget();
        }

        public void write(byte[] b) throws IOException {
            outputStream.write(b);
        }
    }
}
