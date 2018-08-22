package com.fatec.tcc.findfm.Controller;

import android.app.Application;
import android.os.Bundle;

import com.fatec.tcc.findfm.Model.Http.Response.TokenData;

public class FindFM extends Application {

    private Bundle params;
    private static FindFM singleInstance = null;

    private static TokenData tokenData;

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
    public static FindFM getInstance()
    {
        return singleInstance;
    }
    public void setParams(Bundle bundle){
        this.params = bundle;
    }

    /////////////////////////////////////////////////////////////////////////////

    public static TokenData getTokenData() {
        return tokenData;
    }

    public static void setTokenData(TokenData tokenData) {
        FindFM.tokenData = tokenData;
    }
}
