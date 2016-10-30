package com.partymaker.cinaitaren;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.support.multidex.MultiDex;

public class UILApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}