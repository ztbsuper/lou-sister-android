package ztbsuper.lousysterm.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.enums.ExtraParamKeys;

import static ztbsuper.lousysterm.util.LogUtils.debug;
import static ztbsuper.lousysterm.util.LogUtils.error;
import static ztbsuper.lousysterm.util.LogUtils.info;

public class UploadResult extends ActionBarActivity {

    private Button submitBtn = null;
    private Dialog loadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_result);

        submitBtn = (Button) findViewById(R.id.submit_btn);
        loadingDialog = LoadingBuilder.build(this).build();
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
        submitBtn.setClickable(false);
        loadingDialog.show();
        uploadResult();
    }

    private void uploadResult() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                info("get response from server: " + response.toString());
                loadingDialog.hide();
                Dialog dialog = popupSuccessDialog();
                dialog.show();
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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
                loadingDialog.hide();
                error("ERROR: " + volleyError.toString());
            }
        });
        debug("request url: " + requestUrl);
        requestQueue.add(request);
    }

    private void setItemCode(String itemCode) {
        TextView itemCodeTextView = (TextView) findViewById(R.id.item_code);
        itemCodeTextView.setText("");
        itemCodeTextView.setText(itemCode);
    }

    private Dialog popupSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.plain_text_dialog_content);
        return dialog;
    }

}
