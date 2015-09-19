package ztbsuper.lousysterm.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import ztbsuper.lousysterm.R;

/**
 * Created by tbzhang on 9/13/15.
 */
public class LoadingBuilder {
    protected Context context;


    private LoadingBuilder(Context context) {
        this.context = context;
    }

    public static LoadingBuilder build(Context context) {
        return new LoadingBuilder(context);
    }

    public Dialog build() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_content);
        return dialog;
    }
}
