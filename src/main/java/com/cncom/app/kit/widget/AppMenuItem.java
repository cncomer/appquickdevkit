package com.cncom.app.kit.widget;

import android.graphics.drawable.Drawable;

/**
 * Created by bestjoy on 2017/8/16.
 */

public class AppMenuItem {
    public int itemCode;

    public CharSequence title;

    public Drawable icon;

    public int categoryId = 0;
    public int intValue = 0;
    public String stringValue = "";
    public int position = 0;

    public AppMenuItem(int categoryId, int itemCode, int position, CharSequence title) {
        this.itemCode = itemCode;
        this.title = title;
        this.categoryId = categoryId;
        this.position = position;
    }

    public int getItemCode() {
        return itemCode;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public CharSequence getTitle() {
        return title;
    }


    public Drawable getIcon() {
        return icon;
    }
}
