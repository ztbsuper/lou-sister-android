package ztbsuper.lousysterm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;

import ztbsuper.lousysterm.R;

import static ztbsuper.lousysterm.util.LogUtils.info;

public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onClickScan(View self) {
        info("click scan btn");
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onPostResume() {
        info("resume " + this.getClass().getName());
        super.onPostResume();
    }


}
