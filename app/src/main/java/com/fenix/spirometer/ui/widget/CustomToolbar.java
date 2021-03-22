package com.fenix.spirometer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fenix.spirometer.R;

public class CustomToolbar extends FrameLayout {
    private Button mLeft;
    private TextView mCenter;
    private Button mRight;
    private CharSequence mTextLeft;
    private CharSequence mTextCenter;
    private CharSequence mTextRight;
    private OnItemClickListener listener;

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

        LayoutInflater.from(context).inflate(R.layout.widget_toolbar, this);
        mLeft = findViewById(R.id.toolbar_left);
        setLeftText(mTextLeft);
        mLeft.setOnClickListener(view -> {
            if (listener != null) {
                listener.onLeftClick();
            }
        });
        mCenter = findViewById(R.id.toolbar_center);
        mRight = findViewById(R.id.toolbar_right);
        mRight.setOnClickListener(view -> {
            if (listener != null) {
                listener.onRightClick();
            }
        });
        setRightText(mTextRight);
        setCenterText(mTextCenter);
    }

    public void setLeftText(CharSequence seq) {
        if (TextUtils.isEmpty(seq)) {
            mLeft.setVisibility(View.INVISIBLE);
        } else {
            mLeft.setText(seq);
            mLeft.setVisibility(View.VISIBLE);
        }
    }

    public void setCenterText(CharSequence seq) {
        if (TextUtils.isEmpty(seq)) {
            mCenter.setVisibility(View.INVISIBLE);
        } else {
            mCenter.setText(seq);
            mCenter.setVisibility(View.VISIBLE);
        }
    }

    public void setRightText(CharSequence seq) {
        if (TextUtils.isEmpty(seq)) {
            mRight.setVisibility(View.INVISIBLE);
        } else {
            mRight.setText(seq.toString());
            mRight.setVisibility(View.VISIBLE);
        }
    }

    public void clear() {
        setLeftText(null);
        setCenterText(null);
        setRightText(null);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onLeftClick();

        void onRightClick();
    }
}
