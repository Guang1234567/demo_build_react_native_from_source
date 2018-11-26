package com.demo_build_react_native_from_source;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactActivity;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "demo_build_react_native_from_source";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //System.exit(7788);

                //throw new RuntimeException();
            }
        }, 5000);
    }
}
