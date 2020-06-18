package com.pikopako.AppUtill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mukeshs on 19/12/17.
 */

@SuppressLint("AppCompatCustomView")
public class CustomTextViewBold extends TextView {


    Context context;

    public CustomTextViewBold(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public CustomTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public CustomTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "gadugi.ttf");
        this.setTypeface(face);
    }
}
