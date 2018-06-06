package co.hyperverge.hypersnapsample;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import co.hyperverge.hypersnapsdk.HyperSnapSDK;
import co.hyperverge.hypersnapsdk.listeners.CaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.HyperSnapParams;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class HyperSnapSampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HyperSnapSDK.init(this, "", "", HyperSnapParams.Region.India, HyperSnapParams.Product.FACEID);
    }

 }