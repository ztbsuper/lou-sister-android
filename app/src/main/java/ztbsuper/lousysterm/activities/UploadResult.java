package ztbsuper.lousysterm.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.enums.ExtraParamKeys;

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
    }

    private void setItemCode(String itemCode) {
        TextView itemCodeTextView = (TextView) findViewById(R.id.item_code);
        itemCodeTextView.setText("");
        itemCodeTextView.setText(itemCode);
    }


}
