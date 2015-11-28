package ztbsuper.lousysterm.activities;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import ztbsuper.lousysterm.R;

import static ztbsuper.lousysterm.util.LogUtils.info;

public class MainActivity extends Activity {
    private TextView bluetoothStatusTextView;
    private TextView bluetoothDebugConsoleTextView;
    private Button bluetoothDebugButton;
    private BluetoothSPP bluetoothSPP;
    private Button resetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothStatusTextView = (TextView) findViewById(R.id.bluetooth_status);
        bluetoothDebugConsoleTextView = (TextView) findViewById(R.id.bluetooth_response);
        bluetoothDebugButton = (Button) findViewById(R.id.debug_bluetooth);
        resetButton = (Button) findViewById(R.id.reset_button);
        bluetoothSPP = new BluetoothSPP(this);

        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );


        bluetoothSPP.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            @Override
            public void onServiceStateChanged(int state) {
                switch (state) {
                    case BluetoothState.STATE_CONNECTED:
                        break;
                    case BluetoothState.STATE_CONNECTING:
                        break;
                    case BluetoothState.STATE_LISTEN:
                        break;
                    case BluetoothState.STATE_NONE:
                        break;
                }
            }
        });

        bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                info(getString(R.string.bluetooth_connected));
                Toast.makeText(getApplicationContext(),
                        getString(R.string.bluetooth_connected), Toast.LENGTH_SHORT).show();
                bluetoothStatusTextView.setText(getString(R.string.bluetooth_connected));
                bluetoothDebugButton.setClickable(true);
            }

            @Override
            public void onDeviceDisconnected() {
                info(getString(R.string.bluetooth_disconnected));
                Toast.makeText(getApplicationContext(),
                        getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT).show();
                bluetoothStatusTextView.setText(getString(R.string.bluetooth_disconnected));
                bluetoothDebugButton.setClickable(false);
            }

            @Override
            public void onDeviceConnectionFailed() {
                info(getString(R.string.bluetooth_connect_failed));
                Toast.makeText(getApplicationContext(),
                        getString(R.string.bluetooth_connect_failed), Toast.LENGTH_SHORT).show();
                bluetoothStatusTextView.setText(getString(R.string.bluetooth_disconnected));
            }
        });
        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                info("receive data");
                bluetoothDebugConsoleTextView.append(Arrays.toString(data) + "-" + message);
                Toast.makeText(getApplicationContext(),
                        Arrays.toString(data) + "-" + message, Toast.LENGTH_LONG).show();
            }
        });

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
            bluetoothSPP.send(array, true);
            bluetoothSPP.send(array, false);
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
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                    Toast.makeText(this, data.getExtras().getString(BluetoothDevice.EXTRA_DEVICE),
                            Toast.LENGTH_LONG).show();
                }
                BluetoothDevice device = (BluetoothDevice) data.getExtras().get(BluetoothDevice.EXTRA_DEVICE);
                bluetoothSPP.connect(data);
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetoothSPP.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!bluetoothSPP.isBluetoothEnabled()) {
            bluetoothSPP.enable();
        } else {
            if (!bluetoothSPP.isServiceAvailable()) {
                info("start bluetooth spp service");
                bluetoothSPP.setupService();
                bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }
}
