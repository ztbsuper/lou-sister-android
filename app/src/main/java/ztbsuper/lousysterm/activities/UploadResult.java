package ztbsuper.lousysterm.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.enums.ExtraParamKeys;
import ztbsuper.lousysterm.services.BluetoothService;
import ztbsuper.lousysterm.services.CustomBluetoothService;
import ztbsuper.lousysterm.services.DataReceiveListener;

import static ztbsuper.lousysterm.util.LogUtils.debug;
import static ztbsuper.lousysterm.util.LogUtils.error;
import static ztbsuper.lousysterm.util.LogUtils.info;

public class UploadResult extends ActionBarActivity {

    private Button submitBtn = null;
    private Dialog loadingDialog = null;
    private TextView weightInput;
    private BluetoothService bluetoothService = CustomBluetoothService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_result);

        submitBtn = (Button) findViewById(R.id.submit_btn);
        loadingDialog = LoadingBuilder.build(this).build();
        weightInput = (TextView) findViewById(R.id.item_weight);
        setNonClickable();
        final Toast illegalWeightToast = Toast.makeText(getApplicationContext(),
                R.string.illegal_weight, Toast.LENGTH_SHORT);
        new Thread(new Runnable() {


            @Override
            public void run() {
                bluetoothService.setDataReceiveListener(new DataReceiveListener() {
                    private StringBuilder sb = new StringBuilder();


                    @Override
                    public void onDataReceive(int data) {
                        switch (data) {
                            case 0x3D:
                                if (sb.length() != 0) {
                                    Double aDouble = Double.valueOf(sb.toString());
                                    weightInput.setText(String.valueOf(aDouble));
                                    debug("get weight" + sb.toString());
                                    sb.delete(0, sb.length());
                                    submitBtn.setClickable(true);
                                }
                                break;
                            case 0x2D:
                                debug("get negative weight -" + sb.toString());
                                sb.delete(0, sb.length());
                                sb.insert(0, 0);
                                illegalWeightToast.show();
                                submitBtn.setClickable(false);
                                break;
                            default:
                                sb.insert(0, (char) data);
                                break;
                        }

                    }
                });
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setItemCode(getIntent().getStringExtra(ExtraParamKeys.ITEM_CODE.toString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void onClickSubmit(View self) {
        info("click submit btn");
        loadingDialog.show();
        setNonClickable();
        uploadResult();
    }

    private void uploadResult() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                info("get response from server: " + response.toString());
                loadingDialog.dismiss();
                popupClosableDialog(getString(R.string.submit_success), getString(R.string.submit_success), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        UploadResult.this.finish();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        UploadResult.this.finish();
                    }
                });

            }
        };

        HashMap<String, String> map = new HashMap<>();
        map.put("itemCode", ((TextView) findViewById(R.id.item_code)).getText().toString());
        map.put("weight", ((TextView) findViewById(R.id.item_weight)).getText().toString());

        JSONObject requestBody = new JSONObject(map);
        String requestUrl = getString(R.string.server_url) + "storage_record";
        JsonObjectRequest request = new JsonObjectRequest(requestUrl, requestBody, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                popupClosableDialog(getString(R.string.submit_faild), volleyError.toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setClickable();
                        loadingDialog.dismiss();
                    }
                });
                error("ERROR: " + volleyError.toString());
            }
        });
        debug("request url: " + requestUrl);
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void setItemCode(String itemCode) {
        TextView itemCodeTextView = (TextView) findViewById(R.id.item_code);
        itemCodeTextView.setText("");
        itemCodeTextView.setText(itemCode);
    }


    private void setClickable() {
        submitBtn.setClickable(true);
        submitBtn.setBackgroundColor(R.color.theme_accent_1_light);
    }

    private void setNonClickable() {
        submitBtn.setClickable(false);
        submitBtn.setBackgroundColor(R.color.dark_divider);
    }

    private void popupClosableDialog(String title, String content, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content);
        builder.setTitle(title);
        builder.setPositiveButton(getString(R.string.confirm), onClickListener);
        builder.create().show();
    }

}
