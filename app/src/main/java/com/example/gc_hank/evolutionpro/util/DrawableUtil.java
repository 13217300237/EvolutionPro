package com.example.gc_hank.evolutionpro.util;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;

public class DrawableUtil {

    /**
     * 返回一个Drawable对象，可以根据selected状态改变 图标
     *
     * @param normalIcon
     * @param selectedIcon
     * @return
     */
    public static StateListDrawable getStateListDrawable(Context context, int normalIcon, int selectedIcon) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_selected}, ContextCompat.getDrawable(context, selectedIcon));//选中之后的drawable
        drawable.addState(new int[]{}, ContextCompat.getDrawable(context, normalIcon));//正常情况下的drawable
        return drawable;
    }
}
