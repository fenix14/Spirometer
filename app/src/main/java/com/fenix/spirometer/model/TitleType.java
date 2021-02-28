package com.fenix.spirometer.model;

import android.os.Process;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.fenix.spirometer.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TitleType {

    @BgColorId
    private int bgColor;

    private boolean isShowBack;

    @NonNull
    public String title;

    @NonNull
    private String item;

    public TitleType(int bgColor, boolean isShowBack, String title, String item) {
        this.bgColor = bgColor;
        this.isShowBack = isShowBack;
        this.title = title == null ? "" : title;
        this.item = item == null ? "" : item;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public boolean isShowBack() {
        return isShowBack;
    }

    public void setShowBack(boolean showBack) {
        isShowBack = showBack;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getItem() {
        return item;
    }

    public void setItem(@NonNull String item) {
        this.item = item;
    }

    @IntDef({BgColorId.BLUE, BgColorId.WHITE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BgColorId {
        int BLUE = R.color.colorPrimary;
        int WHITE = R.color.white;
    }
}
