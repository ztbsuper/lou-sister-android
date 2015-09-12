package ztbsuper.lousysterm.zxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.google.zxing.Result;

import ztbsuper.lousysterm.zxing.view.ViewfinderView;

public abstract class ScanableActivity extends Activity {
    public abstract void handleResult(Result rawResult, Bundle bundle);

    abstract public Handler getHandler();

    abstract public void drawViewfinder();

    abstract public ViewfinderView getViewfinderView();

    abstract public void handleDecode(Result result, Bitmap barcode);
}
