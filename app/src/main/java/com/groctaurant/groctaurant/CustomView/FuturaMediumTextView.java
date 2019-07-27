package com.groctaurant.groctaurant.CustomView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Danish Rafique on 15-11-2018.
 */
public class FuturaMediumTextView extends android.support.v7.widget.AppCompatTextView {

    public FuturaMediumTextView(Context context) {
        super(context);
        setFont();
    }

    public FuturaMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public FuturaMediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/futura medium bt.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
