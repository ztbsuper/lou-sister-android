package ztbsuper.lousysterm.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.enums.BluetoothDeviceStatus;
import ztbsuper.lousysterm.enums.BluetoothEvent;
import ztbsuper.lousysterm.enums.RequestCode;
import ztbsuper.lousysterm.services.BluetoothService;
import ztbsuper.lousysterm.services.CustomBluetoothService;
import ztbsuper.lousysterm.services.StateChangeListener;

import static ztbsuper.lousysterm.util.LogUtils.info;

public class MainActivity extends Activity {
    private TextView bluetoothStatusTextView;
    private BluetoothService bluetoothService = CustomBluetoothService.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothStatusTextView = (TextView) findViewById(R.id.bluetooth_status);
        bluetoothStatusTextView.setText(
                bluetoothService.getStatus() == BluetoothDeviceStatus.CONNECTED ?
                        R.string.bluetooth_connected :
                        R.string.bluetooth_disconnected
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

    @Override
    protected void onPostResume() {
        info("post resume " + this.getClass().getName());
        super.onPostResume();
    }

    public void onClickBluetoothConnection(View self) {
        Intent intent = new Intent(MainActivity.this, DiscoveryActivity.class);
        startActivityForResult(intent, RequestCode.BLUETOOTH_CONNECTION);
    }

    @Override
    protected void onResume() {
        info("resume " + this.getClass().getName());
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        info("on result");
        if (RequestCode.BLUETOOTH_CONNECTION == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                BluetoothDevice device = (BluetoothDevice) data.getExtras().get(BluetoothDevice.EXTRA_DEVICE);
                bluetoothService.connect(device);
                bluetoothService.setStateChangeListener(new StateChangeListener() {
                    @Override
                    public void onConnectionStart(BluetoothEvent event) {
                        bluetoothStatusTextView.setText(R.string.bluetooth_connecting);
                    }

                    @Override
                    public void onConnected(BluetoothEvent event) {
                        bluetoothStatusTextView.setText(R.string.bluetooth_connected);
                    }

                    @Override
                    public void onDisconnectionStart(BluetoothEvent event) {
                        bluetoothStatusTextView.setText(R.string.bluetooth_disconnecting);
                    }

                    @Override
                    public void onDisconnected(BluetoothEvent event) {
                        bluetoothStatusTextView.setText(R.string.bluetooth_disconnected);
                    }

                    @Override
                    public void onDisconnectionFailed(BluetoothEvent event) {
                        bluetoothStatusTextView.setText(R.string.bluetooth_disconnect_failed);
                    }

                    @Override
                    public void onConnectionFailed(BluetoothEvent event) {
                        bluetoothStatusTextView.setText(R.string.bluetooth_connect_failed);
                    }
                });

            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
