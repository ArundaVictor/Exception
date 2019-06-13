package com.example.exception;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;

import icepick.State;

public class MainActivity extends MultiDexApplication {
    public static Context context;

    private static MainActivity mInstance;

    public void onCreate() {
        super.onCreate();
        this.context = this;
        mInstance = this;


        Fresco.initialize(this.context);




    }

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }


}

