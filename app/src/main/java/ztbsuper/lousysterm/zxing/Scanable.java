package ztbsuper.lousysterm.zxing;

import android.os.Bundle;

import com.google.zxing.Result;

public interface Scanable {
    void handleResult(Result rawResult, Bundle bundle);
}
