package ztbsuper.lousysterm.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.Result;

import java.io.IOException;

import ztbsuper.lousysterm.R;
import ztbsuper.lousysterm.zxing.Scanable;
import ztbsuper.lousysterm.zxing.camera.CameraManager;

import static ztbsuper.lousysterm.util.LogUtils.info;

/**
 * Created by tbzhang on 9/11/15.
 */
public class CaptureActivity extends Activity implements Scanable, SurfaceHolder.Callback {
    private boolean hasSurface = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        info("init " + getClass().getName());
        CameraManager.init(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (!hasSurface)
            initCamera(surfaceHolder);
        else {
            surfaceHolder.addCallback(this);
        }

        info("resume " + getClass().getName());
    }

    @Override
    public void handleResult(Result rawResult, Bundle bundle) {

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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

    }
}
