package co.hyperverge.hypersnapsample;

 import android.app.Application;


import co.hyperverge.hypersnapsdk.HyperSnapSDK;

import co.hyperverge.hypersnapsdk.objects.HyperSnapParams;


public class HyperSnapSampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HyperSnapSDK.init(this, "", "", HyperSnapParams.Region.India);
    }

 }