package com.example.gc_hank.evolutionpro.ui.bottom;

import android.graphics.drawable.StateListDrawable;

public class TabTitle {

    private String routePath;//路由寻址标识，决定这个Tab会显示哪个Fragment
    private int titleName;
    private int textColorStateList;
    private StateListDrawable drawable;

    /**
     * 底部导航栏配置类
     *
     * @param titleName          标题text
     * @param textColorStateList 标题颜色selector
     * @param drawable           标题icon 的selector
     */
    public TabTitle(String routePath, int titleName, int textColorStateList, StateListDrawable drawable) {
        this.routePath = routePath;
        this.titleName = titleName;
        this.drawable = drawable;
        this.textColorStateList = textColorStateList;
    }

    public int getTitleName() {
        return titleName;
    }

    public StateListDrawable getDrawable() {
        return drawable;
    }

    public int getTextColorStateList() {
        return textColorStateList;
    }

    public String getRoutePath() {
        return routePath;
    }
}