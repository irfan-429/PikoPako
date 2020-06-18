package com.pikopako.AppUtill;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by mukeshs on 8/2/18.
 */

public class CustomTextViewNormal extends AppCompatTextView {

    Context context;

    public CustomTextViewNormal(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public CustomTextViewNormal(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public CustomTextViewNormal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public void init(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), "gothic.ttf");
        this.setTypeface(face);
    }
}
