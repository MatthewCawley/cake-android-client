package com.waracle.androidtest;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    public static Context applicationContext;

    public MyApplication() {
        applicationContext = this;
    }
}
