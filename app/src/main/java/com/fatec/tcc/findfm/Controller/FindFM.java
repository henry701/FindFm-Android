package com.fatec.tcc.findfm.Controller;

import android.app.Application;
import android.os.Bundle;

public class FindFM extends Application {

    private Bundle params;
    private static FindFM singleInstance = null;

    public static FindFM getInstance()
    {
        return singleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
    }

    public Bundle getParams() {
        if (params == null) {
            params = new Bundle();
        }
        return params;
    }

    public void setParams(Bundle bundle){
        this.params = bundle;
    }
}
