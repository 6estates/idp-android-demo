package com.sixe.idpandroiddemo.base;

import android.app.Application;

import com.sixe.idp.Idp;

/**
 * Customize the application and initialize IDP
 */
public class IdpApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Pass in the access token to initialize IDP
        Idp.init(this,"");
    }
}
