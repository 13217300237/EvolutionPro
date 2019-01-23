package com.example.gc_hank.evolutionpro.application;

import android.app.Application;

import study.hank.com.api.ZRouter;

public class FTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ZRouter.getInstance().initRegister(this);
    }

}
