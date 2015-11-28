package ztbsuper.lousysterm.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

import app.akexorcist.bluetotohspp.library.BluetoothState;
import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.enums.BluetoothEvent;
import ztbsuper.lousysterm.services.BluetoothService;
import ztbsuper.lousysterm.services.CustomBluetoothService;
import ztbsuper.lousysterm.services.DataReceiveListener;
import ztbsuper.lousysterm.services.StateChangeListener;

import static ztbsuper.lousysterm.util.LogUtils.debug;
import static ztbsuper.lousysterm.util.LogUtils.info;

public class MainActivity extends Activity {
    private TextView bluetoothStatusTextView;
    private TextView bluetoothDebugConsoleTextView;
    private Button bluetoothDebugButton;
    private Button resetButton;
    private BluetoothService bluetoothService = new CustomBluetoothService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothStatusTextView = (TextView) findViewById(R.id.bluetooth_status);
        bluetoothDebugConsoleTextView = (TextView) findViewById(R.id.bluetooth_response);
        bluetoothDebugButton = (Button) findViewById(R.id.debug_bluetooth);
        resetButton = (Button) findViewById(R.id.reset_button);

        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void onClickScan(View self) {
        info("click scan btn");
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
        MainActivity.this.finish();

    }


    public void onClickBluetoothDebug(View self) throws IOException {
        info("click debug btn");
        for (int i = 0; i < 1000; i++) {
            byte[] array = ByteBuffer.allocate(20).putInt(i).array();
            info("send bt:" + Integer.toHexString(i));
        }
    }

    @Override
    protected void onPostResume() {
        info("post resume " + this.getClass().getName());
        super.onPostResume();
    }

    public void onClickBluetoothConnection(View self) {
        Intent intent = new Intent(MainActivity.this, DiscoveryActivity.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    @Override
    protected void onResume() {
        info("resume " + this.getClass().getName());
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        info("on result");
        if (resultCode == Activity.RESULT_OK) {
            BluetoothDevice device = (BluetoothDevice) data.getExtras().get(BluetoothDevice.EXTRA_DEVICE);
            bluetoothService.connect(device);
            bluetoothService.setDataReceiveListener(new DataReceiveListener() {
                @Override
                public void onDataReceive(int data) {
                    debug(String.valueOf((char) data));
                }
            });

            bluetoothService.setStateChangeListener(new StateChangeListener() {
                @Override
                public void onConnectionStart(BluetoothEvent event) {

                }

                @Override
                public void onConnected(BluetoothEvent event) {

                }

                @Override
                public void onDisconnectionStart(BluetoothEvent event) {

                }

                @Override
                public void onDisconnected(BluetoothEvent event) {

                }

                @Override
                public void onDisconnectionFailed(BluetoothEvent event) {

                }

                @Override
                public void onConnectionFailed(BluetoothEvent event) {

                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
