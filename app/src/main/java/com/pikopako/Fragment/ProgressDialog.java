package com.pikopako.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.pikopako.R;
import com.victor.loading.rotate.RotateLoading;

/**
 * Created by mukeshs on 11/5/18.
 */

public class ProgressDialog extends Dialog {

    RotateLoading rotateloading;

    public ProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_progress_dialog);
        rotateloading=(RotateLoading)findViewById(R.id.rotateloading);
        rotateloading.start();

        closeOptionsMenu();
        setCancelable(false);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void show() {
        super.show();
        rotateloading.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    protected void onStop() {
        super.onStop();
        dismiss();
    }
}