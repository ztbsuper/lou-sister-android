package ztbsuper.lousysterm.zxing;

import android.os.Looper;

/**
 * Created by tbzhang on 9/10/15.
 */
public class DecodeThread implements Runnable {
    @Override
    public void run() {
        Looper.prepare();
        Looper.loop();
    }
}
