package com.cs360.project2;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

/** Global app config: force dark mode for the entire app. */
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}