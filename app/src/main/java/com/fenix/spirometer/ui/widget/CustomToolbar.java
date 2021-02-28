package com.fenix.spirometer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.fenix.spirometer.R;

public class CustomToolbar extends FrameLayout {
    private Button mLeft;
    private TextView mCenter;
    private Button mRight;
    private CharSequence mTextLeft;
    private CharSequence mTextCenter;
    private CharSequence mTextRight;

    public CustomToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomToolbar, defStyleAttr, 0);
        mTextLeft = typedArray.getString(R.styleable.CustomToolbar_leftText);
        mTextCenter = typedArray.getString(R.styleable.CustomToolbar_centerText);
        mTextRight = typedArray.getString(R.styleable.CustomToolbar_rightText);

        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this);
        mLeft = findViewById(R.id.toolbar_left);
        mCenter = findViewById(R.id.toolbar_center);
        mRight = findViewById(R.id.toolbar_right);

        setLeft(mTextLeft);
        setCenter(mTextCenter);
        setRight(mTextRight);

    }

    public void setLeft(CharSequence seq) {
        String str = seq == null ? "" : seq.toString();
        Log.d("hff", "CustomToolbar.setLeft = " + str);
        mLeft.setText(str);
    }

    public void setCenter(CharSequence seq) {
        String str = seq == null ? "" : seq.toString();
        Log.d("hff", "CustomToolbar.setCenter = " + str);
        mCenter.setText(str);
    }

    public void setRight(CharSequence seq) {
        String str = seq == null ? "" : seq.toString();
        Log.d("hff", "CustomToolbar.setRight = " + str);
        mRight.setText(str);
    }

    public void clear() {
        Log.d("hff", "CustomToolbar.clear");
        setLeft(null);
        setCenter(null);
        setRight(null);
    }
}
