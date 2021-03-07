package com.fenix.spirometer.model;

import android.os.Process;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.fenix.spirometer.R;
import com.fenix.spirometer.util.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class TitleNavType {

    @Constants.BgType
    private int toolbarType;

    private boolean isShowBack;

    @NonNull
    public String title;

    @NonNull
    private String item;

    @Constants.BgType
    private int navType;

    public TitleNavType(int toolbarType, boolean isShowBack, @NonNull String title, @NonNull String item, int navType) {
        this.toolbarType = toolbarType;
        this.isShowBack = isShowBack;
        this.title = title;
        this.item = item;
        this.navType = navType;
    }

    public int getToolbarType() {
        return toolbarType;
    }

    public void setToolbarType(int toolbarType) {
        this.toolbarType = toolbarType;
    }

    public int getNavType() {
        return navType;
    }

    public void setNavType(int navType) {
        this.navType = navType;
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
