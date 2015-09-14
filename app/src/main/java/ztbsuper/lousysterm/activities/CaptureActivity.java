package ztbsuper.lousysterm.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.enums.ExtraParamKeys;
import ztbsuper.lousysterm.zxing.ScanableActivity;
import ztbsuper.lousysterm.zxing.camera.CameraManager;
import ztbsuper.lousysterm.zxing.camera.CaptureActivityHandler;
import ztbsuper.lousysterm.zxing.decoding.InactivityTimer;
import ztbsuper.lousysterm.zxing.view.ViewfinderView;

import static ztbsuper.lousysterm.util.LogUtils.debug;
import static ztbsuper.lousysterm.util.LogUtils.info;

/**
 * Created by tbzhang on 9/11/15.
 */
public class CaptureActivity extends ScanableActivity implements SurfaceHolder.Callback {
    private boolean hasSurface = false;
    private ViewfinderView viewfinderView = null;
    private CaptureActivityHandler captureActivityHandler = null;
    private Vector<BarcodeFormat> decodeFormats = null;
    private String characterSet = null;
    private ProgressDialog progressDialog = null;
    private InactivityTimer inactivityTimer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        info("init " + getClass().getName());
        CameraManager.init(getApplicationContext());
        progressDialog = new ProgressDialog(CaptureActivity.this);
        inactivityTimer = new InactivityTimer(this);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        debug("has surface? " + hasSurface);
        if (hasSurface)
            initCamera(surfaceHolder);
        else {
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            surfaceHolder.addCallback(this);
        }

        info("resume " + getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (captureActivityHandler != null) {
            captureActivityHandler.quitSynchronously();
            captureActivityHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void handleResult(Result rawResult, Bundle bundle) {

    }

    @Override
    public Handler getHandler() {
        return captureActivityHandler;
    }

    @Override
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }


    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        info("find result:" + result.getText());
        Intent intent = new Intent(this, UploadResult.class);
        intent.putExtra(ExtraParamKeys.ITEM_CODE.toString(), result.getText());
        startActivity(intent);
        this.finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        info("create surface");
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            info("open camera");
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (null == captureActivityHandler)
            captureActivityHandler = new CaptureActivityHandler(this, decodeFormats, characterSet);
    }



}
