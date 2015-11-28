package ztbsuper.lousysterm.services;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

import static ztbsuper.lousysterm.util.LogUtils.debug;
import static ztbsuper.lousysterm.util.LogUtils.error;
import static ztbsuper.lousysterm.util.LogUtils.info;

public class WeightService {
    private static WeightService instance;

    private BluetoothSocket bluetoothSocket;

    private BluetoothSPP bluetoothSPP;

    private OutputStream outputStream;
    private InputStream inputStream;

    public static WeightService getInstance() {
        if (null == instance)
            instance = new WeightService();
        return instance;
    }

    private WeightService() {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setDevice(BluetoothDevice device) throws IOException {
        info("绑定状态" + device.getBondState());
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            device.createBond();
        }
        if (null != bluetoothSocket) {
            bluetoothSocket.close();
            bluetoothSocket.getInputStream().close();
            bluetoothSocket.getOutputStream().close();
        }
        try {
//            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
        } catch (IOException e) {
            error("无法建立蓝牙连接.");
            e.printStackTrace();
        }
        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            error(e.getMessage());
            e.printStackTrace();
        }
        outputStream = bluetoothSocket.getOutputStream();
        inputStream = bluetoothSocket.getInputStream();

        while (true) {
            try {
                debug(String.valueOf(inputStream.read()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAvailable() {
        return null != bluetoothSocket && bluetoothSocket.isConnected();
    }

    public void output(String s) throws IOException {
        debug("output string :" + s);
        outputStream.write(s.getBytes());
    }

    public void readInput(byte[] bytes) throws IOException {
        inputStream.read(bytes);
    }

    public void sendRequest() throws IOException {
        instance.output("R");
    }

    public BluetoothSPP getBluetoothSPP() {
        return bluetoothSPP;
    }

    public void setBluetoothSPP(BluetoothDevice device, Context context) {
        bluetoothSPP = new BluetoothSPP(context);
//        bluetoothSPP.connect(device.getAddress());
//        bluetoothSPP.autoConnect("MacBook Pro");
        bluetoothSPP.setupService();
        bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
        bluetoothSPP.connect(device.getAddress());
    }
}
